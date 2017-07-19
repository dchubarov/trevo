package net.chubarov.trevo.handler;

import net.chubarov.trevo.protocol.ApiRequest;
import net.chubarov.trevo.protocol.ApiResponse;

import java.sql.Connection;

/**
 * Обработчик бизнес запроса, получающий данные в форме {@link ApiRequest}
 * и отвечающего в форме {@link ApiResponse}. Дополнительно получает подключение
 * к БД для осуществления запросов.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public interface ApiHandler {

    /**
     * Метод вызывается для обработки бизнес-запроса.
     * @param request объект данных запроса
     * @param connection объект подключения к БД
     * @return объект данных ответа
     */
    ApiResponse handle(ApiRequest request, Connection connection);

}
