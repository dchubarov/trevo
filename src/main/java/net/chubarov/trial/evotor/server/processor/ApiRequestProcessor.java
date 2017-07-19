package net.chubarov.trial.evotor.server.processor;

import net.chubarov.trial.evotor.handler.ApiHandler;
import net.chubarov.trial.evotor.handler.ApiHandlerFactory;
import net.chubarov.trial.evotor.protocol.ApiRequest;
import net.chubarov.trial.evotor.protocol.ApiRequestParser;
import net.chubarov.trial.evotor.protocol.ApiResponse;
import net.chubarov.trial.evotor.server.ToyServer;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class ApiRequestProcessor extends HttpRequestProcessor {
    private static final Logger logger = Logger.getLogger(ApiRequestProcessor.class.getSimpleName());

    @Override
    protected HttpStatus processHttpRequest(ToyServer server, Map<String, String> requestHeaders, String requestBody,
            Map<String, String> responseHeaders, String[] responseBody) {

        // разобрать запрос от клиента
        ApiRequest request = ApiRequestParser.parse(requestBody);
        if (request == null) {
            return HttpStatus.BAD_REQUEST;
        }

        // получить бизнес-обработчик по виду запроса и обработать запрос
        logger.fine("Получен бизнес запрос: " + request);
        ApiHandler handler = ApiHandlerFactory.getHandler(request);
        if (handler != null) {
            Connection connection = server.getConnectionPool().borrow();
            try {
                ApiResponse response = handler.handle(request, connection);
                if (response != null) {
                    responseBody[0] = response.toXml();
                    return HttpStatus.OK;
                }
            } finally {
                if (connection != null) {
                    server.getConnectionPool().release(connection);
                }
            }
        }

        // не смогли распознать запрос или корректно обработать его
        return HttpStatus.SERVER_ERROR;
    }
}
