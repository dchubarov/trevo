package net.chubarov.trial.evotor.server;

import net.chubarov.trial.evotor.jdbc.JdbcConnectionPool;
import net.chubarov.trial.evotor.server.processor.RequestProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ToyServer {
    private static final Logger logger = Logger.getLogger(ToyServer.class.getSimpleName());

    private final Map<Integer, Supplier<RequestProcessor>> ports = new HashMap<>();
    private final AtomicBoolean shutdownRequested = new AtomicBoolean(false);
    private CountDownLatch runningListenerLatch;
    private ExecutorService executorService;
    private JdbcConnectionPool jdbcPool;

    /**
     * Констуктор вызывается только построителем.
     * @see Builder
     */
    ToyServer() { }

    public void startup() {
        Map<Integer, ListenerThread> listeners = new HashMap<>();
        try {
            for (Map.Entry<Integer, Supplier<RequestProcessor>> e : ports.entrySet()) {
                RequestProcessor requestProcessor = Objects.requireNonNull(e.getValue().get());

                // создать новый серверный сокет для прослушиваемого порта
                int port = e.getKey();
                ServerSocket serverSocket = new ServerSocket(port);
                listeners.put(port, new ListenerThread(this, serverSocket, requestProcessor));

                // установка дополнительных параметров сокета
                serverSocket.setSoTimeout(200);
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "При запуске сервера произошла неисправимая ошибка.", e);
            listeners.values().forEach(l -> closeServerSocket(l.getServerSocket()));
            return;
        }

        // добавляем обработчик завершения JVM корректно останавливающий сервер
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));

        // запуск всех прослушивателей
        runningListenerLatch = new CountDownLatch(listeners.size());
        listeners.values().forEach(Thread::start);

        logger.severe("Сервер запущен и находится в ожидании входящих соединений.");
    }

    public void requestShutdown() {
        executorService.shutdown();
        shutdownRequested.compareAndSet(false, true);
    }

    public JdbcConnectionPool getConnectionPool() {
        return jdbcPool;
    }

    private void shutdown() {
        requestShutdown();

        while (runningListenerLatch.getCount() > 0) {
            try {
                runningListenerLatch.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // если пул потоков не смог завершиться в процессе пытаемся завершить принудительно
        if (!executorService.isTerminated()) {
            executorService.shutdownNow();
        }

        // с большой вероятностью логгеры здесь уже не работают, выводит окончательное сообщение в консоль
        System.out.println("Работа сервера завершена.");
    }

    private static void closeServerSocket(ServerSocket serverSocket) {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "При закрытии серверного порта произошла ошибка.", e);
            }
        }
    }

    private static void closeClientSocket(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, "Ошибка при закрытии соединения.", e);
            }
        }
    }

    public static class Builder {
        private ToyServer prototype;

        public Builder listen(int port, Supplier<RequestProcessor> processorSupplier) {
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Недопустимый номер порта: " + port);
            }

            getPrototype().ports.put(port, Objects.requireNonNull(processorSupplier));
            return this;
        }

        public Builder withThreadPool(ExecutorService executorService) {
            getPrototype().executorService = Objects.requireNonNull(executorService);
            return this;
        }

        public Builder withConnectionPool(JdbcConnectionPool jdbcPool) {
            getPrototype().jdbcPool = Objects.requireNonNull(jdbcPool);
            return this;
        }

        public ToyServer build() {
            ToyServer instance = getPrototype();

            if (instance.ports.isEmpty()) {
                throw new IllegalStateException("Не задан ни один порт для прослушивания.");
            }

            if (instance.jdbcPool == null) {
                throw new IllegalStateException("Не создан пул подключений к БД.");
            }

            if (instance.executorService == null) {
                instance.executorService = Executors.newCachedThreadPool();
            }

            prototype = null;
            return instance;
        }

        private ToyServer getPrototype() {
            if (prototype == null) {
                prototype = new ToyServer();
            }
            return prototype;
        }
    }

    private class ListenerThread extends Thread {
        private final ServerSocket serverSocket;
        private final RequestProcessor requestProcessor;

        ListenerThread(ToyServer server, ServerSocket serverSocket, RequestProcessor requestProcessor) {
            this.serverSocket = Objects.requireNonNull(serverSocket);
            this.requestProcessor = Objects.requireNonNull(requestProcessor);
            setName(getClass().getSimpleName() + "-" + serverSocket.getLocalPort());
        }

        @Override
        public void run() {
            logger.info("Запущен прослушиватель порта " + serverSocket.getLocalPort());
            try {
                boolean acceptConnections = true;
                do {
                    try {
                        Socket socket = serverSocket.accept();
                        logger.log(Level.FINE, "Получен запрос от " + socket);
                        executorService.submit(() -> {
                            try {
                                requestProcessor.process(ToyServer.this, socket);
                            } catch (Throwable e) {
                                logger.log(Level.WARNING, "Ошибка при обработке запроса.", e);
                            } finally {
                                closeClientSocket(socket);
                            }
                        });
                    } catch (SocketTimeoutException e) {
                        if (Thread.currentThread().isInterrupted() || shutdownRequested.get()) {
                            acceptConnections = false;
                        }
                    }
                } while (acceptConnections);
            } catch (IOException | SecurityException | IllegalBlockingModeException e) {
                logger.log(Level.SEVERE, "Произошла неисправимая ошибка при обработке входящего запроса.", e);
            }

            //
            // перед закрытием серверного сокета необходимо дождаться завершения всех
            // обработчиков запросов иначе клиенты не смогут получить ответ
            //
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // закрыть серверный сокет и уведомить сервер о завершении потока слушателя
            closeServerSocket(serverSocket);
            runningListenerLatch.countDown();

            // getLocalPort() работает в том числе для закрытого сокета
            logger.info("Завершен поток слушателя порта " + serverSocket.getLocalPort());
        }

        ServerSocket getServerSocket() {
            return serverSocket;
        }
    }
}
