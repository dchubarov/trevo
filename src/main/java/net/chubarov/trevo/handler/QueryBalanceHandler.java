package net.chubarov.trevo.handler;

import net.chubarov.trevo.protocol.ApiStatusCode;
import net.chubarov.trevo.protocol.ApiRequest;
import net.chubarov.trevo.protocol.ApiResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Обработчик бизнес запроса 'GET-BALANCE'.
 *
 * <p>Находит в БД пользователя по его регистрационному имени, проверяет, что пароль
 * переданный в запросе соответствует тому, который хранится в БД и в случае успеха
 * возвращает значение баланса.</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
class QueryBalanceHandler implements ApiHandler {
    private static final Logger logger = Logger.getLogger(RegisterClientHandler.class.getSimpleName());

    @Override
    public ApiResponse handle(ApiRequest request, Connection connection) {
        ApiResponse response = new ApiResponse();
        String sql = "SELECT PASSWORD, BALANCE FROM CLIENT WHERE LOGIN=?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.getProperty("login"));
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    if (Objects.equals(request.getProperty("password"), rs.getString(1))) {
                        BigDecimal balance = rs.getBigDecimal(2);
                        response.putProperty("balance", balance.toPlainString());
                    } else {
                        // пароль запроса не совпадает с таковым в БД
                        response.setStatusCode(ApiStatusCode.INVALID_PASSWORD);
                    }
                } else {
                    // возвращен пустой набор результатов - пользователь не существует
                    response.setStatusCode(ApiStatusCode.NOT_EXISTS);
                }
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Ошибка при выполнении операции с БД.", e);
            response.setStatusCode(ApiStatusCode.TECHNICAL_ERROR);
        }

        return response;
    }
}
