/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataBaseContent.Generic;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author partizanka
 */
public abstract class ResultSetProcessor {
    public abstract void processResultSet(ResultSet rs) throws SQLException;
}
