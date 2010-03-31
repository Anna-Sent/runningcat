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
public class DataTypes extends Data {

    @Override
    protected DataElement getElement(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String data_type_name_en = rs.getString(2);
        return new DataType(id, data_type_name_en);
    }

    protected DataTypes() {
        super();
        fields = new String[]{"id", "data_type_name_en"};
        from = "mdl_problemstatement_data_type";
    }

    @Override
    protected String getWhereString(int id) {
        return "id=" + id;
    }
    private static DataTypes instance = null;

    public static DataTypes getInstance() {
        return instance == null ? (instance = new DataTypes()) : instance;
    }

    public DataType getDataTypeById(int id) {
        return (DataType) super.getElementById(id);
    }
}
