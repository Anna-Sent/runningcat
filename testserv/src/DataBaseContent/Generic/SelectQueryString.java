/*
 */
package DataBaseContent.Generic;

/**
 *
 * @author partizanka
 */
public class SelectQueryString {

    private String query;

    /**
     *
     * @param fields
     * @param from
     * @param where
     */
    public SelectQueryString(
            String[] fields,
            String from,
            String where) {
        query = "select ";
        for (int i = 0; i < fields.length; ++i) {
            query += fields[i] + (i < fields.length - 1 ? "," : " ");
        }
        query += "from " + from;
        if (where != null) {
            query += " where " + where;
        }
    }

    /**
     *
     * @param fields
     * @param from
     */
    public SelectQueryString(
            String[] fields,
            String from) {
        this(fields, from, null);
    }

    /**
     *
     * @return
     */
    public String getString() {
        return query;
    }
}
