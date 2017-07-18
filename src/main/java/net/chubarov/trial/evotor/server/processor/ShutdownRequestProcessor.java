package net.chubarov.trial.evotor.server.processor;

import net.chubarov.trial.evotor.server.ToyServer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ShutdownRequestProcessor extends LineRequestProcessor {
    private static final Logger logger = Logger.getLogger(ShutdownRequestProcessor.class.getSimpleName());

    @Override
    protected void processRequest(ToyServer server, BufferedReader requestReader,
            BufferedWriter responseWriter) throws IOException {
        String line = requestReader.readLine();
        if ("QUIT".equals(line)) {
            logger.info("Получена команда QUIT, инициируем остановку сервера.");
            responseWriter.write("OK");
            server.requestShutdown();
        }
    }
}
