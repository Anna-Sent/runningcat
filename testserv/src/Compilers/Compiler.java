package Compilers;

import FileOperations.FileOperator;
import Compilers.Exceptions.CompilationErrorException;
import Compilers.Exceptions.CompilationInternalServerErrorException;
import Compilers.Exceptions.CompilationTimeLimitExceededException;
import DataBaseContent.Problems;
import DataProcessing.InputDataProcessor;
import DataProcessing.OutputDataProcessor;
import DataProcessing.SimpleInputDataProcessor;
import DataProcessing.SimpleOutputDataProcessor;
import FileOperations.Exceptions.CanNotCreateTemporaryDirectoryException;
import FileOperations.Exceptions.CanNotCreateTemporaryFileException;
import FileOperations.Exceptions.CanNotWriteFileException;
import IOGenerating.InputFromFileGenerator;
import IOGenerating.InputGenerator;
import IOGenerating.OutputFromFileGenerator;
import IOGenerating.OutputGenerator;
import ProcessExecuting.Exceptions.ProcessExecutingException;
import ProcessExecuting.Exceptions.ProcessNotRunningException;
import ProcessExecuting.ProcessExecutor;
import Program.Program;
import ProgramTesting.Exceptions.RunTimeErrorException;
import ProgramTesting.Exceptions.TestingInternalServerErrorException;
import ProgramTesting.Exceptions.TestingTimeLimitExceededException;
import ProgramTesting.Exceptions.UnsuccessException;
import ProgramTesting.ProgramTester;
import Shared.Configuration;
import Shared.ExitCodes;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author partizanka
 */
public class Compiler {

    /**
     *
     */
    protected StringBuffer message;
    private long TIME_LIMIT = 30000;
    private int lang;

    /**
     * Конструктор принимает в качестве параметра код языка программирования.
     * Затем этот код используется для взятия параметров конфигурации.
     *
     * @param lang код языка программирования
     */
    public Compiler(int lang) {
        this.lang = lang;
    }

    private InputStream getCompileInput(ProcessExecutor e) throws
            ProcessNotRunningException,
            CompilationInternalServerErrorException {
        int fd = Configuration.getCompileOutputFileDescriptor(lang);
        switch (fd) {
            case 2:
                return e.getErrorStream();
            case 1:
                return e.getInputStream();
            default:
                throw new CompilationInternalServerErrorException("Compilation output file descriptor is incorrect: got " + fd + ", exptected 1 or 2; language id is " + lang);
        }
    }

    /**
     * Метод запускает процесс компиляции программы program.
     * Программа должна быть не null.
     * Перед вызовом этого метода должен быть вызван метод Program.prepare().
     * Вывод компилятора - стандартный или вывод ошибок (соответствующие
     * файловые дескрипторы - 1 и 2) - определяется используемым компилятором,
     * читается из файла настроек.
     * Время работы компилятора ограничивается значением TIME_LIMIT.
     * Успешность компиляции определяется по наличию бинарного файла.
     * Из сообщения компиляции имя файла с исходным кодом заменяется на слово
     * &lt;code&gt;.
     * Процессу компилятору передается в качестве рабочей директории директория,
     * в которой находится файл с исходным кодом программы. В качестве параметра
     * передается относительное имя файла с исходным кодом.
     *
     * @param program конкретная программа, должна быть не null, должен быть вызван
     * метод Program.prepare()
     * @throws CompilationErrorException ошибка компиляции
     * @throws CompilationInternalServerErrorException в процессе компиляции
     * на сервере произошла ошибка
     * @throws CompilationTimeLimitExceededException процесс компиляции занял
     * слишком много времени
     */
    public void compile(Program program) throws
            CompilationErrorException,
            CompilationInternalServerErrorException,
            CompilationTimeLimitExceededException {
        message = new StringBuffer(100);
        ProcessExecutor executor = new ProcessExecutor(
                program.getCompileCmd(),
                program.getDirPath(),
                TIME_LIMIT);
        BufferedReader reader = null;
        try {
            executor.execute(); // throws ProcessExecutingException
            reader = new BufferedReader(
                    new InputStreamReader(
                    getCompileInput(executor))); // throws ProcessExecutingException
            String line;
            while ((line = reader.readLine()) != null) { // throws IOException
                message.append(line + "\n");
            }
            int code = executor.waitForExit(); // throws ProcessExecutingException, InterruptedException
            System.err.println("Compilation process exited with code " + code);
            System.err.println("Compilation process was run for " + executor.getWorkTime());
            if (executor.isOutOfTime()) {
                throw new CompilationTimeLimitExceededException(
                        "Compilation process is out of time");
            }
            if (!program.canExecute()) {
                processMessage(program);
                throw new CompilationErrorException(message.toString());
            }
        } catch (IOException e) {
            try {
                int code = executor.waitForExit(); // throws ProcessExecutingException, InterruptedException
                System.err.println("Compilation process exited with code " + code);
                System.err.println("Compilation process was run for " + executor.getWorkTime());
            } catch (ProcessNotRunningException ex) {
                ex.printStackTrace();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            throw new CompilationInternalServerErrorException(
                    "Input/output error while compilation: " + e);
        } catch (ProcessExecutingException e) {
            throw new CompilationInternalServerErrorException(
                    "Compilation process running error: " + e);
        } catch (InterruptedException e) {
            throw new CompilationInternalServerErrorException(
                    "Interrupted: " + e);
        } finally {
            FileOperator.close(reader);
        }
    }

    private void processMessage(Program p) {
        int index;
        index = message.indexOf(p.getSrcFileName());
        while (index > -1) {
            message = message.replace(
                    index, index + p.getSrcFileName().length(), "&lt;code&gt;");
            index = message.indexOf(p.getSrcFileName());
        }
    }

    /**
     * Для запуска тестирования класса компилятора Compiler необходимо
     * запустить сервер баз данных MySQL, с базой данных Moodle, с
     * установеленным модулем problemstatement, с задачами и тестами, а также
     * необходим файл конфигурации testserv.cfg.xml.
     *
     * @param argv аргументы командной строки
     */
    @SuppressWarnings("static-access")
    public static void main(String argv[]) {
        if (Configuration.loadFromFile("testserv.cfg.xml") != 0) {
            System.err.println("Configuration file not found or parse error");
            return;
        }
        Connection connection = null;
        Program p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(Configuration.getURL(),
                    Configuration.getUser(), Configuration.getPassword());
            connection.setTransactionIsolation(connection.TRANSACTION_READ_COMMITTED);
            System.out.println("URL: " + Configuration.getURL());
            System.out.println("Connection: " + connection);
            //Content.loadAll(connection);
            Problems.connection = connection;
            //p = new Program(0, "#include <stdio.h>\n"+
            //  "main() {sdf printf(\"hello world\"); return 0; }\n",
            //  Problems.getInstance().getProblemById(1));
            p = new Program(1, "const nmax=1000;\n"
                    + "var mass:array[1..nmax]of integer;\n"
                    + "i,j,m,n:integer;\n"
                    + "begin\n"
                    + "read(n,m);\n"
                    + "for i:=1 to n do\n"
                    + "mass[i]:=i;\n"
                    + "i:=1;\n"
                    + "while n>1 do begin\n"
                    + "i:=(i+m-1) mod n;\n"
                    + "if i=0 then i:=n;\n"
                    + "for j:=i to n-1 do\n"
                    + "mass[j]:=mass[j+1];\n"
                    + "n:=n-1;\n"
                    + "end;\n"
                    + "writeln(mass[1]);\n"
                    + "end.\n"/*"var a: ^Integer; begin new(a);dispose(a);dispose(a);end."*/, Problems.getInstance().getProblemById(6));
            p.prepare();
            Compiler c = CompilerFactory.getInstance().getCompiler(p.lang);
            c.compile(p);
            System.err.println("success compilation");
            InputGenerator inputGenerator = new InputFromFileGenerator(p);
            OutputGenerator outputGenerator = new OutputFromFileGenerator(p);
            InputDataProcessor inputDataProcessor = new SimpleInputDataProcessor();
            OutputDataProcessor outputDataProcessor = new SimpleOutputDataProcessor();
            ProgramTester tester = new ProgramTester(
                    inputGenerator, outputGenerator,
                    inputDataProcessor, outputDataProcessor);
            tester.execute(p);
        } catch (ClassNotFoundException e) {
            System.err.println("Mysql lib not found: " + e);
        } catch (SQLException e) {
            System.err.println("SQL error occurs: " + e);
        } catch (CompilationInternalServerErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program compilation: " + e.getMessage());
        } catch (TestingInternalServerErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program testing: " + e.getMessage());
        } catch (CompilationErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.COMPILATION_ERROR));
            System.err.println(e.getMessage());
            System.err.println("Compilation error: " + e.getMessage());
        } catch (CompilationTimeLimitExceededException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.COMPILATION_ERROR));
            System.err.println(e.getMessage());
            System.err.println("Compilation process is out of time");
        } catch (TestingTimeLimitExceededException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.TIME_OUT_ERROR));
            System.err.println(e.getMessage());
            System.err.println("Testing process is out of time");
        } catch (CanNotCreateTemporaryDirectoryException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program temp directory creation: " + e.getMessage());
        } catch (CanNotCreateTemporaryFileException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program temp file creation: " + e.getMessage());
        } catch (CanNotWriteFileException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program file writing: " + e.getMessage());
        } catch (UnsuccessException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.UNSUCCESS));
            System.err.println("Failed tests: " + e.getMessage());
        } catch (RunTimeErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.RUNTIME_ERROR));
            System.err.println(e.getMessage());
            System.err.println("Failed tests: " + e.getMessage());
        } finally {
            if (p != null) {
                p.close();
            }
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error while closing connection: " + e);
            }
        }
    }
}
