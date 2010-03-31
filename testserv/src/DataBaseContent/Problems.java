/*
 */
package DataBaseContent;

import DataBaseContent.Generic.Data;
import DataBaseContent.Generic.DataElement;
import DataBaseContent.Generic.ResultSetProcessor;
import DataBaseContent.Generic.SelectQueryString;
import DataBaseContent.Generic.StatementProcessor;
import java.util.HashMap;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author partizanka
 */
public class Problems extends Data {

    @Override
    protected DataElement getElement(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        String description = rs.getString(2);
        String restrictions = rs.getString(3);
        String samples = rs.getString(4);
        String name = rs.getString(5);
        String testsdir = rs.getString(6);
        ArrayList<Integer> igenerators = getIGenerators(id, connection);
        HashMap<Integer, Integer> ioparams = getIOParams(id, connection);
        ArrayList<Integer> solutions = getSolutions(id, connection);
        return new Problem(id, description, restrictions, samples, name, testsdir, solutions, igenerators, ioparams);
    }

    protected Problems() {
        super();
        fields = new String[]{"id", "description", "restrictions", "samples", "name", "testsdir"};
        from = "mdl_problemstatement_problem";
    }

    @Override
    protected String getWhereString(int id) {
        return "id=" + id;
    }
    private static Problems instance = null;

    public static Problems getInstance() {
        return instance == null ? (instance = new Problems()) : instance;
    }

    private HashMap<Integer, Integer> getIOParams(int problem_id, Connection connection) {
        class ioparamsReader extends ResultSetProcessor {

            private HashMap<Integer, Integer> ioparams;

            public ioparamsReader(HashMap<Integer, Integer> ioparams) {
                this.ioparams = ioparams;
            }

            @Override
            public void processResultSet(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int n = rs.getInt(1);
                    int format_id = rs.getInt(2);
                    ioparams.put(new Integer(n), new Integer(format_id));
                }
            }
        }
        HashMap<Integer, Integer> ioparams = new HashMap<Integer, Integer>();
        new StatementProcessor().processStatement(connection,
                new ioparamsReader(ioparams),
                new SelectQueryString(
                new String[]{"n", "format_id"},
                "mdl_problemstatement_problem_input_output_params",
                "problem_id=" + problem_id));
        return ioparams;
    }

    private ArrayList<Integer> getIGenerators(int problem_id, Connection connection) {
        class igeneratorsReader extends ResultSetProcessor {

            private ArrayList<Integer> igenerators;

            public igeneratorsReader(ArrayList<Integer> igenerators) {
                this.igenerators = igenerators;
            }

            @Override
            public void processResultSet(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int input_generator_id = rs.getInt(1);
                    igenerators.add(new Integer(input_generator_id));
                }
            }
        }
        ArrayList<Integer> igenerators = new ArrayList<Integer>();
        new StatementProcessor().processStatement(connection,
                new igeneratorsReader(igenerators),
                new SelectQueryString(
                new String[]{"input_generator_id"},
                "mdl_problemstatement_problem_input_generator",
                "problem_id=" + problem_id));
        return igenerators;
    }

    private ArrayList<Integer> getSolutions(int problem_id, Connection connection) {
        class solutionsReader extends ResultSetProcessor {

            private ArrayList<Integer> solutions;

            public solutionsReader(ArrayList<Integer> solutions) {
                this.solutions = solutions;
            }

            @Override
            public void processResultSet(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    int solution_id = rs.getInt(1);
                    solutions.add(new Integer(solution_id));
                }
            }
        }
        ArrayList<Integer> solutions = new ArrayList<Integer>();
        new StatementProcessor().processStatement(connection,
                new solutionsReader(solutions),
                new SelectQueryString(
                new String[]{"solution_id"},
                "mdl_problemstatement_problem_solution",
                "problem_id=" + problem_id));
        return solutions;
    }

    public Problem getProblemById(int id) {
        return (Problem) super.getElementById(id);
    }
}
