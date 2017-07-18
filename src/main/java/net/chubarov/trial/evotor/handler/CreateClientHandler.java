package net.chubarov.trial.evotor.handler;

import net.chubarov.trial.evotor.protocol.ApiErrorCode;
import net.chubarov.trial.evotor.protocol.ApiRequest;
import net.chubarov.trial.evotor.protocol.ApiResponse;

import java.sql.Connection;

/**
 * <p>TODO add documentation...</p>
 * @author Dmitry Chubarov
 */
class CreateClientHandler implements ApiHandler {
    @Override
    public ApiResponse handle(ApiRequest request, Connection connection) {
        return new ApiResponse(ApiErrorCode.ALREADY_EXISTS);
    }
}
