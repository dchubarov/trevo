package net.chubarov.trevo.jdbc;

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
public class SimpleConnectionPool implements ConnectionPool {
    private static final Logger logger = Logger.getLogger(SimpleConnectionPool.class.getSimpleName());

    private final List<Connection> available = new LinkedList<>();
    private final List<Connection> used = new LinkedList<>();
    private final String url;
    private final String user;
    private final String password;

    /**
     * Создает новый экземпляр {@link SimpleConnectionPool}.
     * @param driver полное имя класса драйвера JDBC.
     * @param url строка подключения к БД.
     * @param user пользователь БД.
     * @param password пароль пользователя БД.
     * @param initialPoolSize количество соединений для начального резервирования.
     */
    public SimpleConnectionPool(String driver, String url, String user, String password, int initialPoolSize) {
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

    @Override
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

    @Override
    public synchronized void release(Connection connection) {
        if (connection != null) {
            if (used.remove(connection)) {
                available.add(connection);
            } else {
                logger.warning("Попытка возврата в пул подключения, которое не принадлежит этому пулу.");
            }
        }
    }

    @Override
    public void shutdown() {
        closeAll(available);
        closeAll(used);
    }

    private void closeAll(List<Connection> connections) {
        if (connections != null) {
            connections.forEach(c -> {
                try {
                    c.close();
                } catch (SQLException ignore) { }
            });
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
