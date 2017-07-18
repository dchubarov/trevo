package net.chubarov.trial.evotor.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Простейший пул JDBC-подключений.</p>
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public class JdbcConnectionPool {
    private static final Logger logger = Logger.getLogger(JdbcConnectionPool.class.getSimpleName());

    private final List<Connection> available = new LinkedList<>();
    private final List<Connection> used = new LinkedList<>();
    private final String url;
    private final String user;
    private final String password;

    public JdbcConnectionPool(String driver, String url, String user, String password, int initialPoolSize) {
        this.url = url;
        this.user = user;
        this.password = password;

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Не обнаружен драйвер JDBC: " + driver);
        }

        initialize(initialPoolSize);
    }

    public synchronized Connection borrow() {
        Connection connection = null;
        try {
            connection = (available.isEmpty() ? connect() : available.remove(0));
            used.add(connection);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка создания нового подключения к базе данных: " + url, e);
        }
        return connection;
    }

    public synchronized void release(Connection connection) {
        if (connection != null) {
            if (used.remove(connection)) {
                available.add(connection);
            } else {
                logger.warning("Попытка возврата в пул подключения, которое не принадлежит этому пулу.");
            }
        }
    }

    private void initialize(int initialPoolSize) {
        logger.info("Инициализация пула подключений к базе данных: " + url);
        try {
            for (int i = 0; i < initialPoolSize; i++) {
                available.add(connect());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Ошибка создания зарезервированного подключения к базе данных: " + url, e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
