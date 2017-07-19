package net.chubarov.trial.evotor.handler;

import net.chubarov.trial.evotor.protocol.ApiErrorCode;
import net.chubarov.trial.evotor.protocol.ApiRequest;
import net.chubarov.trial.evotor.protocol.ApiResponse;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>TODO add documentation...</p>
 * @author Dmitry Chubarov
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
                        response.setErrorCode(ApiErrorCode.INVALID_PASSWORD);
                    }
                } else {
                    // возвращен пустой набор результатов - пользователь не существует
                    response.setErrorCode(ApiErrorCode.NOT_EXISTS);
                }
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Ошибка при выполнении операции с БД.", e);
            response.setErrorCode(ApiErrorCode.TECHNICAL_ERROR);
        }

        return response;
    }
}
