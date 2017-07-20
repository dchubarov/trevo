package net.chubarov.trevo;

import net.chubarov.trevo.jdbc.SimpleConnectionPool;
import net.chubarov.trevo.server.NetworkServer;
import net.chubarov.trevo.server.processor.ApiRequestProcessor;
import net.chubarov.trevo.server.processor.ShutdownRequestProcessor;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Пусковой класс сервера.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class Bootstrap {
    private static final Logger logger = Logger.getLogger(Bootstrap.class.getSimpleName());

    private static final String DEFAULT_CONFIG_FILE = "server.properties";
    private static final int DEFAULT_CONNECTION_POOL_SIZE = 10;
    private static final int DEFAULT_ADMIN_PORT = 9050;
    private static final int DEFAULT_HTTP_PORT = 8080;

    public static void main(String[] args) {
        configureLogging();
        try {
            String configPath = (args.length < 1 ? DEFAULT_CONFIG_FILE : args[0]);
            NetworkServer server = createServer(configPath);
            server.startup();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Ошибка запуска сервера.", e);
        }
    }

    private static NetworkServer createServer(String configFile) throws Exception {
        Properties configuration = loadConfiguration(configFile);

        // создаем пул соединений БД
        SimpleConnectionPool connectionPool = new SimpleConnectionPool(
                configuration.getProperty("jdbc.driver"),
                configuration.getProperty("jdbc.url"),
                configuration.getProperty("jdbc.user"),
                configuration.getProperty("jdbc.password"),
                getIntegerProperty(configuration, "jdbc.pool.size",
                        DEFAULT_CONNECTION_POOL_SIZE));

        // базовые компоненты сервера
        NetworkServer.Builder serverBuilder = new NetworkServer.Builder()
                .withThreadPool(Executors.newCachedThreadPool())
                .withConnectionPool(connectionPool);

        // добавляем слушатель административного порта
        int adminPort = getIntegerProperty(configuration, "server.port.admin", DEFAULT_ADMIN_PORT);
        serverBuilder.listen(adminPort, ShutdownRequestProcessor::new);

        // добавляем слушателей HTTP портов
        String httpPorts = configuration.getProperty("server.port.api");
        if (httpPorts != null && !httpPorts.isEmpty()) {
            Stream.of(httpPorts.split(",")).mapToInt(Integer::parseInt)
                    .forEach(p -> serverBuilder.listen(p, ApiRequestProcessor::new));
        } else {
            serverBuilder.listen(DEFAULT_HTTP_PORT, ApiRequestProcessor::new);
        }

        return serverBuilder.build();
    }

    private static Properties loadConfiguration(String configFile) throws IOException {
        logger.info("Конфигурационный файл: " + configFile);
        try (Reader reader = new InputStreamReader(new FileInputStream(configFile))) {
            Properties properties = new Properties();
            properties.load(reader);
            return properties;
        }
    }

    private static int getIntegerProperty(Properties configuration, String name, int def) {
        String value = configuration.getProperty(name);
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignore) { }
        }
        return def;
    }

    private static void configureLogging() {
        try (InputStream is = Bootstrap.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации логгера: " + e.getLocalizedMessage());
        }
    }
}
