package net.chubarov.trial.evotor.server.processor;

import net.chubarov.trial.evotor.server.ToyServer;

import java.io.*;
import java.net.Socket;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public abstract class LineRequestProcessor implements RequestProcessor {

    /**
     *
     * @param server
     * @param requestReader
     * @param responseWriter
     * @throws IOException
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
