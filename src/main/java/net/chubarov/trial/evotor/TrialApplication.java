package net.chubarov.trial.evotor;

import net.chubarov.trial.evotor.jdbc.JdbcConnectionPool;
import net.chubarov.trial.evotor.server.ToyServer;
import net.chubarov.trial.evotor.server.processor.ApiRequestProcessor;
import net.chubarov.trial.evotor.server.processor.ShutdownRequestProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class TrialApplication {

    public static void main(String[] args) {
        configureLogging();

        ToyServer server = new ToyServer.Builder()
                .listen(8080, ApiRequestProcessor::new)
                .listen(9050, ShutdownRequestProcessor::new)
                .withConnectionPool(createConnectionPool())
                .build();

        server.startup();
    }

    private static JdbcConnectionPool createConnectionPool() {
        return new JdbcConnectionPool(
                "oracle.jdbc.OracleDriver",
                "jdbc:oracle:thin:@//10.31.10.21:1521/SIRIUSDEV",
                "SIRIUS_9_DA_CLIENT",
                "q1",
                10);
    }

    private static void configureLogging() {
        try (InputStream is = TrialApplication.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (IOException e) {
            System.err.println("Ошибка загрузки конфигурации логгера: " + e.getLocalizedMessage());
        }
    }
}
