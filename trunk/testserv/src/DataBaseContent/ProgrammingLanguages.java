/*
 */
package DataBaseContent;

import DataBaseContent.Generic.Data;
import DataBaseContent.Generic.DataElement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author partizanka
 */
public class ProgrammingLanguages extends Data {

    /**
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    @Override
    protected DataElement getElement(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String language_name = rs.getString(2);
//        String suffix = rs.getString(3);
        return new ProgrammingLanguage(id, language_name/*, suffix*/);
    }

    /**
     *
     */
    protected ProgrammingLanguages() {
        super();
        fields = new String[]{"id", "language_name"/*, "suffix"*/};
        from = "mdl_problemstatement_programming_language";
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
    private static ProgrammingLanguages instance = null;

    /**
     *
     * @return
     */
    public static ProgrammingLanguages getInstance() {
        return instance == null ? (instance = new ProgrammingLanguages()) : instance;
    }

    /**
     *
     * @param id
     * @return
     */
    public ProgrammingLanguage getProgrammingLanguageById(int id) {
        return (ProgrammingLanguage) super.getElementById(id);
    }
}
