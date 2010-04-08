package DataBaseContent.Generic;

/**
 * Класс формирует строку select-запроса.
 *
 * @author partizanka
 */
public class SelectQueryString {

    private String query;

    /**
     * Конструктор генерирует строку select-запроса.
     *
     * @param fields массив полей, которые нужно вытянуть из запроса
     * @param from таблицы или объединение таблиц
     * @param where условие выборки
     */
    public SelectQueryString(
            String[] fields,
            String from,
            String where) {
        query = "SELECT ";
        for (int i = 0; i < fields.length; ++i) {
            query += fields[i] + (i < fields.length - 1 ? "," : " ");
        }
        query += "FROM " + from;
        if (where != null) {
            query += " WHERE " + where;
        }
    }

    /**
     * Вызов аналогичен SelectQueryString(fields, from, null). Если условие
     * выборки не задано.
     *
     * @param fields массив полей, которые нужно вытянуть из запроса
     * @param from таблицы или объединение таблиц
     */
    public SelectQueryString(
            String[] fields,
            String from) {
        this(fields, from, null);
    }

    /**
     * Метод возвращает строку запроса.
     *
     * @return строка select-запроса
     */
    public String getString() {
        return query;
    }
}
