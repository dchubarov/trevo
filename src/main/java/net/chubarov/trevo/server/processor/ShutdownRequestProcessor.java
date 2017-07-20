package net.chubarov.trevo.server.processor;

import net.chubarov.trevo.server.NetworkServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Процессор запроса, реализующий протокол, состоящий из единственной
 * команды QUIT, завершающей работу сервера.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ShutdownRequestProcessor extends LineRequestProcessor {
    private static final Logger logger = Logger.getLogger(ShutdownRequestProcessor.class.getSimpleName());

    @Override
    protected void processRequest(NetworkServer server, BufferedReader requestReader,
            BufferedWriter responseWriter) throws IOException {
        String line = requestReader.readLine();
        if ("QUIT".equals(line)) {
            logger.info("Получена команда QUIT, инициируем остановку сервера.");
            responseWriter.write("BYE");
            server.requestShutdown();
        }
    }
}
