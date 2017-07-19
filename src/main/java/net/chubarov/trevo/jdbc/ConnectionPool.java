package net.chubarov.trevo.jdbc;

import java.sql.Connection;

/**
 * Контрактный интерфейс пула JDBC-соединений.
 *
 * @author Dmitry Chubarov
 * @since 1.0.0
 */
public interface ConnectionPool {

   /**
    * Позволяет одолжить подключение у пула. Одолженное подключение не
    * будет одолжено другому потребителю до освобождения
    * методом {@link #release(Connection)}.
    * @return объект подключения к БД.
    */
   Connection borrow();

   /**
    * Возвращает подключение в пул.
    * @param connection подключение для возврата в пул.
    */
   void release(Connection connection);

   /**
    * Закрывает все открытые соединения.
    */
   void shutdown();

}
