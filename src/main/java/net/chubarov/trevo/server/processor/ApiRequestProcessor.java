package net.chubarov.trevo.server.processor;

import net.chubarov.trevo.handler.ApiHandler;
import net.chubarov.trevo.handler.ApiHandlerFactory;
import net.chubarov.trevo.protocol.ApiRequest;
import net.chubarov.trevo.protocol.ApiResponse;
import net.chubarov.trevo.server.ToyServer;
import net.chubarov.trevo.protocol.ApiRequestParser;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Верхнеуровневый процессор, извлекающий бизнес-запрос из HTTP, и использующий
 * обработчики для выполнения запроса. Возвращает клиенту XML-ответ.
 *
 * @see ApiRequest
 * @see ApiHandler
 * @see ApiHandlerFactory
 * @see ApiResponse
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
                    responseHeaders.put(CONTENT_TYPE, "text/xml");
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
