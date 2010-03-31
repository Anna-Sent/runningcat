/*
 */
package DataBaseContent;

import DataBaseContent.Generic.Data;
import DataBaseContent.Generic.DataElement;
import java.sql.*;

/**
 *
 * @author partizanka
 */
public class Solutions extends Data {

    @Override
    protected DataElement getElement(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String source = rs.getString(2);
        int language_id = rs.getInt(3);
        return new Solution(id, source, language_id);
    }

    protected Solutions() {
        super();
        fields = new String[]{"id", "source", "language_id"};
        from = "mdl_problemstatement_solution";
    }

    @Override
    protected String getWhereString(int id) {
        return "id=" + id;
    }
    private static Solutions instance = null;

    public static Solutions getInstance() {
        return instance == null ? (instance = new Solutions()) : instance;
    }
}
