package net.chubarov.trevo.server.processor;

import net.chubarov.trevo.server.ToyServer;

import java.io.*;
import java.net.Socket;

/**
 * Абстрактный процессор запроса обрабатывающий текстовые данные, поступающие от клиента построчно.
 * Классы-наследники не могут переопределить метод {@link RequestProcessor#process(ToyServer, Socket)},
 * они должны вместо этого реализовать метод {@link #processRequest(ToyServer, BufferedReader, BufferedWriter)},
 * позволяющий использовать {@code BufferedReader} и {@code BufferedWriter} для чтения/записи данных.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public abstract class LineRequestProcessor implements RequestProcessor {

    /**
     * Метод считывает данные запроса из входного потока и отправляет ответ в выходной.
     * @param server сервер, которому поступил запрос от клиента.
     * @param requestReader читатель входного потока данных.
     * @param responseWriter писатель выходного потока данных.
     * @throws IOException если в процессе обмена данными произошла ошибка.
     */
    protected abstract void processRequest(ToyServer server, BufferedReader requestReader,
            BufferedWriter responseWriter) throws IOException;

    @Override
    public final void process(ToyServer server, Socket socket) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
            processRequest(server, reader, writer);
            writer.newLine();
            writer.flush();
        }
    }
}
