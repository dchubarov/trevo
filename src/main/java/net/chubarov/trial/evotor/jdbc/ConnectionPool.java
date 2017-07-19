package net.chubarov.trial.evotor.jdbc;

import java.sql.Connection;

/**
 * <p>TODO add documentation...</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public interface ConnectionPool {

   Connection borrow();

   void release(Connection connection);

   void shutdown();

}
