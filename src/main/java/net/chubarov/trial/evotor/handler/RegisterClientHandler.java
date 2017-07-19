package net.chubarov.trial.evotor.handler;

import net.chubarov.trial.evotor.protocol.ApiErrorCode;
import net.chubarov.trial.evotor.protocol.ApiRequest;
import net.chubarov.trial.evotor.protocol.ApiResponse;
import oracle.jdbc.OracleTypes;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Обработчик запроса регистрации клиента.
 *
 * <p>Используется специфика Oracle.</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
class RegisterClientHandler implements ApiHandler {
    private static final Logger logger = Logger.getLogger(RegisterClientHandler.class.getSimpleName());

    @Override
    public ApiResponse handle(ApiRequest request, Connection connection) {
        ApiResponse response = new ApiResponse();
        String sql = "{CALL CLIENT_PKG.REGISTER(?, ?, ?)}";
        try (CallableStatement statement = connection.prepareCall(sql)) {
            String login = request.getProperty("login");
            statement.setString(1, login);
            statement.setString(2, request.getProperty("password"));
            statement.registerOutParameter(3, OracleTypes.NUMBER);
            statement.execute();

            int clientId = statement.getInt(3);
            if (clientId > 0) {
                response.putProperty("Client-Id", Integer.toString(clientId));
                logger.info("Клиент с регистрационным именем " + login
                        + " успешно зарегистрирован с идентификатором " + clientId);
            } else {
                logger.warning("Клиент с регистрационным именем " + login + " уже зарегистрирован.");
                response.setErrorCode(ApiErrorCode.ALREADY_EXISTS);
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Ошибка при выполнении операции с БД.", e);
            response.setErrorCode(ApiErrorCode.TECHNICAL_ERROR);
        }

        return response;
    }
}
