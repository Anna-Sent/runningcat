/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataBaseContent.Generic;

import DataBaseContent.*;
import java.sql.Connection;

/**
 *
 * @author partizanka
 */
public class Content {
    public static void loadAll(Connection connection) {
        DataTypes.connection=connection;
        DataTypes.getInstance().loadAll();

        IGenerators.connection=connection;
        IGenerators.getInstance().loadAll();

        ParseFormats.connection=connection;
        ParseFormats.getInstance().loadAll();

        Problems.connection=connection;
        Problems.getInstance().loadAll();

        Solutions.connection=connection;
        Solutions.getInstance().loadAll();

        ProgrammingLanguages.connection=connection;
        ProgrammingLanguages.getInstance().loadAll();
    }
}
