/*
 */
package DataBaseContent.Generic;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author partizanka
 */
public abstract class ResultSetProcessor {

    /**
     *
     * @param rs
     * @throws SQLException
     */
    public abstract void processResultSet(ResultSet rs) throws SQLException;
}
