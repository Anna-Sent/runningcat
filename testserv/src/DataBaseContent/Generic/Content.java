package DataBaseContent.Generic;

import DataBaseContent.IGenerators;
import DataBaseContent.DataTypes;
import DataBaseContent.ParseFormats;
import DataBaseContent.Problems;
import DataBaseContent.Solutions;
import DataBaseContent.ProgrammingLanguages;
import java.sql.Connection;

/**
 *
 * @author partizanka
 */
public class Content {

    /**
     * 
     * @param connection
     */
    public static void loadAll(Connection connection) {
        DataTypes.connection = connection;
        DataTypes.getInstance().loadAll();

        IGenerators.connection = connection;
        IGenerators.getInstance().loadAll();

        ParseFormats.connection = connection;
        ParseFormats.getInstance().loadAll();

        Problems.connection = connection;
        Problems.getInstance().loadAll();

        Solutions.connection = connection;
        Solutions.getInstance().loadAll();

        ProgrammingLanguages.connection = connection;
        ProgrammingLanguages.getInstance().loadAll();
    }
}
