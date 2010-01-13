/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataBaseContent.Generic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author partizanka
 */
public class StatementProcessor {
    public static void processStatement(
            Connection connection,
            ResultSetProcessor rsp,
            SelectQueryString query) {
        Statement statement = null;
        ResultSet rs = null;
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery(query.getString());
            rsp.processResultSet(rs);
        } catch (SQLException e) {
                e.printStackTrace();
        } finally {
            try {
                if (rs!=null && !rs.isClosed()) rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (statement!=null && !statement.isClosed()) statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
