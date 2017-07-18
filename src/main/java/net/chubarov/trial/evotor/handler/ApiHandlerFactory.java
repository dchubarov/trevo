package net.chubarov.trial.evotor.handler;

import net.chubarov.trial.evotor.protocol.ApiRequest;
import net.chubarov.trial.evotor.protocol.ApiRequestType;

/**
 * Фабрика отвечает за создание обработчика по виду запроса.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public final class ApiHandlerFactory {

    public static ApiHandler getHandler(ApiRequest request) {
        return (request != null && request.getRequestType() != null ?
                getHandlerByType(request.getRequestType()) : null);
    }

    private static ApiHandler getHandlerByType(ApiRequestType requestType) {
        ApiHandler handler = null;
        switch (requestType) {
            case CREATE:
                handler = new CreateClientHandler();
                break;

            case QUERY_BALANCE:
                handler = new QueryBalanceHandler();
                break;
        }
        return handler;
    }

    /** Предотвращает создание экземпляров */
    private ApiHandlerFactory() {}
}
