package net.chubarov.trial.evotor.handler;

import net.chubarov.trial.evotor.protocol.ApiRequest;
import net.chubarov.trial.evotor.protocol.ApiResponse;

import java.sql.Connection;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public interface ApiHandler {

    ApiResponse handle(ApiRequest request, Connection connection);

}
