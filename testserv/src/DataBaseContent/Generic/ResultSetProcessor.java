package DataBaseContent.Generic;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Абстрактный класс обработчик ResultSet - множества записей, выбранных из
 * таблицы БД.
 *
 * @author partizanka
 */
public abstract class ResultSetProcessor {

    /**
     * Обрабатывает множество записей, выбранных из БД.
     *
     * @param rs множество записей, выбранных из таблицы
     * @throws SQLException ошибка БД
     */
    public abstract void processResultSet(ResultSet rs) throws SQLException;
}
