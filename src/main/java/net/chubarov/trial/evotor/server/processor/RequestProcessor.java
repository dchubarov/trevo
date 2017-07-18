package net.chubarov.trial.evotor.server.processor;

import net.chubarov.trial.evotor.server.ToyServer;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>TODO add documentation...</p>
 * @author Dmitry Chubarov
 */
public interface RequestProcessor {

    void process(ToyServer server, Socket socket) throws IOException;

}
