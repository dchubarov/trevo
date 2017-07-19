package net.chubarov.trevo.handler;

import net.chubarov.trevo.protocol.ApiRequest;
import net.chubarov.trevo.protocol.ApiResponse;
import net.chubarov.trevo.protocol.ApiStatusCode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Обработчик бизнес-запроса 'CREATE-AGT'.
 *
 * <p>Вызывает хранимую процедуру Oracle для регистрации пользователя.</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
class RegisterClientHandler implements ApiHandler {
    private static final Logger logger = Logger.getLogger(RegisterClientHandler.class.getSimpleName());

    /** Внутренний номер числового типа драйвера Oracle */
    private static final int ORACLE_NUMBER = 2;

    @Override
    public ApiResponse handle(ApiRequest request, Connection connection) {
        ApiResponse response = new ApiResponse();
        String sql = "{CALL CLIENT_PKG.REGISTER(?, ?, ?)}";
        try (CallableStatement statement = connection.prepareCall(sql)) {
            String login = request.getProperty("login");
            statement.setString(1, login);
            statement.setString(2, request.getProperty("password"));
            statement.registerOutParameter(3, ORACLE_NUMBER);
            statement.execute();

            int clientId = statement.getInt(3);
            if (clientId > 0) {
                response.putProperty("Client-Id", Integer.toString(clientId));
                logger.info("Клиент с регистрационным именем " + login
                        + " успешно зарегистрирован с идентификатором " + clientId);
            } else {
                logger.warning("Клиент с регистрационным именем " + login + " уже зарегистрирован.");
                response.setStatusCode(ApiStatusCode.ALREADY_EXISTS);
            }
        } catch (Throwable e) {
            logger.log(Level.WARNING, "Ошибка при выполнении операции с БД.", e);
            response.setStatusCode(ApiStatusCode.TECHNICAL_ERROR);
        }

        return response;
    }
}
