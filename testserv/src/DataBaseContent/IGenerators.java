package DataBaseContent;

import DataBaseContent.Generic.Data;
import DataBaseContent.Generic.DataElement;
import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 * @author partizanka
 */
public class IGenerators extends Data {

    /**
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    @Override
    protected DataElement getElement(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String source = rs.getString(2);
        int language_id = rs.getInt(3);
        return new IGenerator(id, source, language_id);
    }

    /**
     *
     */
    protected IGenerators() {
        super();
        fields = new String[]{"id", "source", "language_id"};
        from = "mdl_problemstatement_input_generator";
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    protected String getWhereString(int id) {
        return "id=" + id;
    }
    private static IGenerators instance = null;

    /**
     *
     * @return
     */
    public static IGenerators getInstance() {
        return instance == null ? (instance = new IGenerators()) : instance;
    }
}
