package net.chubarov.trevo.handler;

import net.chubarov.trevo.protocol.ApiRequestType;
import net.chubarov.trevo.protocol.ApiRequest;

/**
 * Фабрика создает обработчики бизнес-запросов исходя из содержимого самого запроса.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public final class ApiHandlerFactory {

    /**
     * Возвращает обработчик для переданного бизнес-запроса.
     * @param request запрос для которого нужен обработчик, не {@code null}.
     * @return экземпляр обработчика, может вернуть {@code null}.
     */
    public static ApiHandler getHandler(ApiRequest request) {
        return (request != null && request.getRequestType() != null ?
                getHandlerByType(request.getRequestType()) : null);
    }

    private static ApiHandler getHandlerByType(ApiRequestType requestType) {
        ApiHandler handler = null;
        switch (requestType) {
            case CREATE:
                handler = new RegisterClientHandler();
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
