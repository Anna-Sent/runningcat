/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataBaseContent;

import DataBaseContent.Generic.Data;
import DataBaseContent.Generic.DataElement;
import java.sql.*;

/**
 *
 * @author partizanka
 */
public class ParseFormats extends Data {
    @Override
    protected DataElement getElement(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String comment = rs.getString(2);
        int data_type_id = rs.getInt(3);
        String read_function = rs.getString(4);
        String write_function = rs.getString(5);
        return new ParseFormat(id, comment, data_type_id, read_function, write_function);
    }
    protected ParseFormats() {
        super();
        fields = new String[] {"id", "comment", "data_type_id", "read_function", "write_function"};
        from = "mdl_problemstatement_parse_format";
    }
    @Override
    protected String getWhereString(int id) {
        return "id="+id;
    }
    private static ParseFormats instance=null;
    public static ParseFormats getInstance() {
        return instance==null?(instance = new ParseFormats()):instance;
    }
}
