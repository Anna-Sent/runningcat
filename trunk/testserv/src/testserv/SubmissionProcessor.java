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
import IOGenerating.InputFromFileGenerator;
import IOGenerating.InputGenerator;
import IOGenerating.OutputFromFileGenerator;
import IOGenerating.OutputGenerator;
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
            problemId;
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
        }
        condition = "submission.processed='" + NOT_PROCESSED + "' and "
                + "submission.langid in (" + langsstr + ")";
    }

    private boolean fillData(ResultSet rs) {
        Reader reader = null;
        try {
            submissionId = rs.getInt("id");
            userId = rs.getInt("userid");
            langId = rs.getInt("langid");
            problemstatementId = rs.getInt("problemstatement");
            problemId = rs.getInt("problem_id");
            testsdir = rs.getString("testsdir");
            name = rs.getString("name");
            lastname = rs.getString("lastname");
            firstname = rs.getString("firstname");
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
        OutputGenerator outputGenerator = new OutputFromFileGenerator(p);
        InputDataProcessor inputDataProcessor = new SimpleInputDataProcessor();
        OutputDataProcessor outputDataProcessor = new SimpleOutputDataProcessor();
        ProgramTester tester = new ProgramTester(
                inputGenerator, outputGenerator,
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
            res = ExitCodes.RUNTIME_ERROR;
            return false;
        } catch (TestingTimeLimitExceededException e) {
            comment.append(ExitCodes.getMsg(ExitCodes.TIME_OUT_ERROR));
            error.append("Failed tests: " + e.getMessage());
            res = ExitCodes.TIME_OUT_ERROR;
            return false;
        }
    }

    private class myRSProc extends ResultSetProcessor {

        @Override
        public void processResultSet(ResultSet rs) throws SQLException {
            while (rs.next()) { // обрабатываем текущую запись
                comment.setLength(0);
                error.setLength(0);
                res = -1;
                if (fillData(rs)) { // заполнить необходимые данные
                    if (setInProcessStatus()) { // установить статус "обрабатывается"
                        if (Problems.getInstance().contains(problemId)) { // если соответствующая задача найдена
                            Program p = new Program( // создать экземпляр программы
                                    langId, programtext,
                                    Problems.getInstance().getProblemById(problemId));
                            if (createProgram(p)) { // создать необходимые файлы на диске
                                if (compileProgram(p)) { // компилировать программу
                                    if (testProgram(p)) { // тестировать программу
                                        res = ExitCodes.SUCCESS; // тесты пройдены
                                        comment.append(ExitCodes.getMsg(ExitCodes.SUCCESS));
                                    } // ошибка при тестировании программы
                                } // ошибка при компиляции программы
                                p.close(); // удаляем созданные файлы
                            } // ошибка при создании необходимых файлов
                        } else { // задача не найдена
                            comment.append(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
                            error.append("Problem not found :-(");
                            res = ExitCodes.INTERNAL_ERROR;
                        } // установить
                        setProcessedStatus(); // установить статус "обработано"
                        System.err.println("UPDATED [" + name + "]" + " user=" + firstname + " " + lastname);
                    } // ошибка sql при установке статуса
                } // ошибка sql при чтении очередной записи
                System.err.println(comment + "\n" + error);
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
                "mdl_problemstatement_submissions submission "
                + "join mdl_problemstatement problemstatement on (submission.problemstatement=problemstatement.id) "
                + "join mdl_problemstatement_problem problem on (problemstatement.problem_id=problem.id) "
                + "join mdl_user user on (user.id=submission.userid) ",
                condition)); // get correct compilers
    }

    private boolean setInProcessStatus() {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.executeUpdate(
                    "UPDATE mdl_problemstatement_submissions " +
                    "SET processed = '" + IN_PROCESS + "' " +
                    "WHERE id='" + submissionId + "';");
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
            statement.executeUpdate("UPDATE mdl_problemstatement_submissions "
                    + "SET processed='" + PROCESSED + "', "
                    + "submissioncomment='" + com + "', "
                    + "errormessage='" + er + "', "
                    + "succeeded='" + res + "' "
                    + "WHERE id=" + submissionId + ";");
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
