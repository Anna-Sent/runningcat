/*
 */
package DataBaseContent.Generic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 *
 * @author partizanka
 */
public class Data {

    /**
     *
     */
    public static Connection connection;
    private HashMap<Integer, DataElement> elements = new HashMap<Integer, DataElement>();

    /**
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    protected DataElement getElement(ResultSet rs) throws SQLException {
        return null;
    }

    ;

    private class myRSProc extends ResultSetProcessor {

        @Override
        public void processResultSet(ResultSet rs) throws SQLException {
            while (rs.next()) {
                DataElement e = getElement(rs);
                elements.put(new Integer(e.id), e);
            }
        }
    }

    @SuppressWarnings("static-access")
    private void load(SelectQueryString query) {
        StatementProcessor.processStatement(
                connection, new myRSProc(), query);
    }
    /**
     *
     */
    /**
     *
     */
    protected String fields[], from;

    /**
     *
     */
    public void loadAll() {
        load(new SelectQueryString(fields, from));
    }

    /**
     *
     * @param id
     * @return
     */
    protected String getWhereString(int id) {
        return "";
    }

    /**
     *
     * @param id
     */
    public void loadOne(int id) {
        load(new SelectQueryString(fields, from, getWhereString(id)));
    }

    /**
     *
     * @param id
     * @return
     */
    protected DataElement getElementById(int id) {
        if (!elements.containsKey(new Integer(id))) {
            loadOne(id);
        }
        return elements.get(new Integer(id));
    }

    /**
     *
     * @param id
     * @return
     */
    public boolean contains(int id) {
        return elements.containsKey(new Integer(id));
    }
}
