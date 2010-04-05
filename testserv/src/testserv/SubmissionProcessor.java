/*
 */
package testserv;

import Compilers.Compiler;
import Compilers.CompilerFactory;
import Compilers.Exceptions.CompilationErrorException;
import Compilers.Exceptions.CompilationInternalServerErrorException;
import Compilers.Exceptions.CompilationTimeLimitExceededException;
import DataBaseContent.Generic.ResultSetProcessor;
import DataBaseContent.Generic.SelectQueryString;
import DataBaseContent.Generic.StatementProcessor;
import DataBaseContent.Problems;
import DataProcessing.InputDataProcessor;
import DataProcessing.OutputDataProcessor;
import DataProcessing.SimpleInputDataProcessor;
import DataProcessing.SimpleOutputDataProcessor;
import FileOperations.Exceptions.CanNotCreateTemporaryDirectoryException;
import FileOperations.Exceptions.CanNotCreateTemporaryFileException;
import FileOperations.Exceptions.CanNotWriteFileException;
import FileOperations.FileOperator;
import InputGenerating.InputFromFileGenerator;
import InputGenerating.InputGenerator;
import Program.Program;
import ProgramTesting.Exceptions.RunTimeErrorException;
import ProgramTesting.Exceptions.TestingInternalServerErrorException;
import ProgramTesting.Exceptions.TestingTimeLimitExceededException;
import ProgramTesting.Exceptions.UnsuccessException;
import ProgramTesting.ProgramTester;
import Shared.Configuration;
import Shared.ExitCodes;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author partizanka
 */
public class SubmissionProcessor {

    private int submissionId, userId, langId, problemstatementId,
            problem_id;
    private String testsdir, name, lastname, firstname, programtext;
    private StringBuffer comment, error;
    private int res;
    private String condition;
    private final int NOT_PROCESSED = 0,
            PROCESSED = 1,
            IN_PROCESS = 2;
    private Connection connection;

    /**
     * 
     * @param connection
     */
    public SubmissionProcessor(Connection connection) {
        this.connection = connection;
        comment = new StringBuffer();
        error = new StringBuffer();
        Integer[] langs = Configuration.getLangs();
        String langsstr = "";
        for (int i = 0; i < langs.length; ++i) {
            langsstr += ("'" + langs[i] + "'" + ((i < langs.length - 1) ? "," : ""));
            //System.err.println(langs[i]);
        }
        condition = "submission.processed='" + NOT_PROCESSED + "' and " +
                "submission.langid in (" + langsstr + ")";
    }

    private boolean fillData(ResultSet rs) {
        Reader reader = null;
        try {
            submissionId = rs.getInt("id");
            userId = rs.getInt("userid");
            langId = rs.getInt("langid");
            problemstatementId = rs.getInt("problemstatement");
            problem_id = rs.getInt("problem_id");
            testsdir = rs.getString("testsdir");
            name = rs.getString("name");
            lastname = rs.getString("lastname");
            firstname = rs.getString("firstname");
            //programtext = rs.getString("programtext");
            StringBuffer sbuf = new StringBuffer();
            reader = rs.getCharacterStream("programtext");
            char ch;
            while ((ch = (char) reader.read()) != 65535) {
                sbuf.append(ch);
            }
            programtext = sbuf.toString();
            return true;
        } catch (SQLException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while submission data reading: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } catch (IOException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while 'programtext' field processing (i/o error): " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } finally {
            FileOperator.close(reader);
        }
    }

    private boolean createProgram(Program p) {
        try {
            p.prepare();
            return true;
        } catch (CanNotCreateTemporaryDirectoryException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while program temp directory creation: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } catch (CanNotCreateTemporaryFileException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while program temp file creation: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } catch (CanNotWriteFileException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while program file writing: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        }
    }

    private boolean compileProgram(Program p) {
        try {
            if (!Configuration.isCompiled(p.lang)) {
                return true;
            }
            Compiler c = CompilerFactory.getInstance().getCompiler(p.lang);
            c.compile(p);
            return true;
        } catch (CompilationInternalServerErrorException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while program compilation: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } catch (CompilationErrorException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.COMPILATION_ERROR) + "\n");
            comment.append(e.getMessage());
            error.append("Compilation error: " + e.getMessage());
            res = ExitCodes.COMPILATION_ERROR;
            return false;
        } catch (CompilationTimeLimitExceededException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.COMPILATION_ERROR) + "\n");
            comment.append(e.getMessage());
            error.append("Compilation error: compilation process is out of time");
            res = ExitCodes.COMPILATION_ERROR;
            return false;
        }
    }

    private boolean testProgram(Program p) {
        InputGenerator inputGenerator = new InputFromFileGenerator(p);
        InputDataProcessor inputDataProcessor = new SimpleInputDataProcessor();
        OutputDataProcessor outputDataProcessor = new SimpleOutputDataProcessor();
        ProgramTester tester = new ProgramTester(inputGenerator,
                inputDataProcessor, outputDataProcessor);
        try {
            tester.execute(p);
            return true;
        } catch (UnsuccessException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.UNSUCCESS));
            error.append("Failed tests: " + e.getMessage());
            res = ExitCodes.UNSUCCESS;
            return false;
        } catch (TestingInternalServerErrorException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while program testing: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } catch (RunTimeErrorException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.RUNTIME_ERROR) + "\n");
            comment.append(e.getMessage());
            error.append("Failed tests: " + e.getMessage());
            System.err.println(p.text);
            res = ExitCodes.RUNTIME_ERROR;
            return false;
        } catch (TestingTimeLimitExceededException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.TIME_OUT_ERROR));
            error.append("Failed tests: " + e.getMessage());
            System.err.println(p.text);
            res = ExitCodes.TIME_OUT_ERROR;
            return false;
        }
    }

    private class myRSProc extends ResultSetProcessor {

        @Override
        public void processResultSet(ResultSet rs) throws SQLException {
            while (rs.next()) {
                comment.setLength(0);
                error.setLength(0);
                res = -1;
                if (fillData(rs)) {
                    if (setInProcessStatus()) {
                        if (Problems.getInstance().contains(problem_id)) {
                            // create program object
                            Program p = new Program(
                                    langId, programtext,
                                    Problems.getInstance().getProblemById(problem_id));
                            // create necessary files on disk
                            if (createProgram(p)) {
                                // compile program
                                if (compileProgram(p)) {
                                    // if compilation successfull, then test program
                                    if (testProgram(p)) {
                                        res = ExitCodes.SUCCESS; // yahoo!
                                        comment.append(ExitCodes.getMsg(ExitCodes.SUCCESS));
                                    } // error while testing program
                                } // error while compilation program
                                // files exist, delete them
                                p.close();
                            } // error while creating necessary files
                        } else {
                            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
                            error.append("Problem not found :-(");
                            res = ExitCodes.INTERNAL_ERROR;
                        }
                        // previous setting status was successfull, then try to set processed status
                        setProcessedStatus();
                    } // error while setting status
                } // error while filling submission data
                // Put error messages
                System.err.println("UPDATED [" + name + "]" + " user=" + firstname + " " + lastname + "\n" + comment + "\n" + error);
                System.err.println("=======================================================================");
            } // end while
        }
    }

    /**
     *
     */
    @SuppressWarnings("static-access")
    public void processSubmission() {
        StatementProcessor.processStatement(connection, new myRSProc(),
                new SelectQueryString(
                new String[]{
                    "submission.id",
                    "submission.problemstatement",
                    "submission.userid",
                    "submission.programtext",
                    "submission.langid",
                    "problem.testsdir",
                    "problemstatement.name",
                    "problemstatement.problem_id",
                    "user.lastname",
                    "user.firstname"},
                "mdl_problemstatement_submissions submission " +
                "join mdl_problemstatement problemstatement on (submission.problemstatement=problemstatement.id) " +
                "join mdl_problemstatement_problem problem on (problemstatement.problem_id=problem.id) " +
                "join mdl_user user on (user.id=submission.userid) ",
                condition)); // get correct compilers
    }

    private boolean setInProcessStatus() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(
                    "update mdl_problemstatement_submissions set processed = '" + IN_PROCESS + "' where id='" + submissionId + "'");
            return true;
        } catch (SQLException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while setting 'is process' status: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } finally {
            try {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean setProcessedStatus() {
        Statement statement = null;
        try {
            String com = comment.toString(), er = error.toString();
            com = com.replace("'", "\\'");
            er = er.replace("'", "\\'");
            statement = connection.createStatement();
            statement.executeUpdate("update mdl_problemstatement_submissions " +
                    "set processed='" + PROCESSED + "', " +
                    "submissioncomment='" + com + "', " +
                    "errormessage='" + er + "', " +
                    "succeeded='" + res + "' " +
                    "where id=" + submissionId + ";");
            return true;
        } catch (SQLException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            error.append("Error while setting 'processed' status: " + e.getMessage());
            res = ExitCodes.INTERNAL_ERROR;
            return false;
        } finally {
            try {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
