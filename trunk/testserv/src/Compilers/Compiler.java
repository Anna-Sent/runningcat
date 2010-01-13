package Compilers;

import FileOperations.FileOperator;
import java.io.*;
import ProcessExecuting.*;
import ProcessExecuting.Exceptions.*;
// for test
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
import InputGenerating.InputFromFileGenerator;
import InputGenerating.InputGenerator;
import Program.Program;
import ProgramTesting.Exception.RunTimeErrorException;
import ProgramTesting.Exception.TestingInternalServerErrorException;
import ProgramTesting.Exception.TestingTimeLimitExceededException;
import ProgramTesting.Exception.UnsuccessException;
import ProgramTesting.ProgramTester;
import Shared.Configuration;
import Shared.ExitCodes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author partizanka
 */
public class Compiler {
    protected StringBuffer message;
    private long TIME_LIMIT = 30000;
    private int lang;

    public Compiler(int lang) {
        this.lang=lang;
    }

    protected String[] getCmd(Program p) {
        String[] cmd = Configuration.getCompilerCommand(p.lang);
        if (cmd!=null)
            for (int i=0;i<cmd.length;++i) {
                cmd[i] = cmd[i].replace("<bin>", p.getBinFileName());
                cmd[i] = cmd[i].replace("<src>", p.getSrcFileName());
            }
        return cmd;
    }
    //public abstract String srcFileSuffix();
    private InputStream getCompileInput(ProcessExecutor e) throws ProcessNotRunningException {
        switch (Configuration.getOutputFD(lang)) {
            case 2: return e.getErrorStream();
            case 1: return e.getInputStream();
            default: return null;
        }
    }
    /**
     * Compiles the program. Creates temp file with program's source code.
     * Runs compiler. If compilation is successfull, binary file is created.
     * 
     * @param program Program to compile.
     * @return one of {@link ExitCodes} values
     */
    public void compile(Program program) throws CompilationErrorException, CompilationInternalServerErrorException, CompilationTimeLimitExceededException {
        message = new StringBuffer(100);
        ProcessExecutor executor = new ProcessExecutor(
                getCmd(program),
                program.getDirPath(),
                TIME_LIMIT);
        BufferedReader reader = null;
        try {
            executor.execute();
            reader = new BufferedReader(new InputStreamReader(getCompileInput(executor)));
            String line;
            while ((line = reader.readLine()) != null)
                message.append(line + "\n");
        } catch (IOException e) {
            throw new CompilationInternalServerErrorException("Input/output error while compilation: " + e);
        } catch (ProcessExecutingException e) {
            throw new CompilationInternalServerErrorException("Compilation process running error: " + e);
        } finally {
            try {
                if (executor.isRunning()) {
                    int code = executor.waitForExit();
                    System.err.println("Compilation process exited with code "+code);
                    System.err.println("Compilation process was run for "+executor.getWorkTime());
                    if (executor.isOutOfTime())
                        throw new CompilationTimeLimitExceededException("Program is out of time");
                    if (!program.canExecute()) {
                        processCompileMessage(program);
                        throw new CompilationErrorException(message.toString());
                    }
                }
            } catch (InterruptedException e) {
                throw new CompilationInternalServerErrorException("Interrupted: " + e);
            } catch (ProcessNotRunningException e) {
                throw new CompilationInternalServerErrorException("Compilation process is not running: "+e);
            } finally {
                FileOperator.close(reader);
            }
        }
    }
    private void processCompileMessage(Program p) {
        int index = message.indexOf(p.getSrcFileName());
        while (index > -1) {
            message = message.replace(index, index+p.getSrcFileName().length(), "");
            index = message.indexOf(p.getSrcFileName());
        }
    }
    /*protected void processCompileMessage() { // depends on fpc output
        String[] lines = message.toString().split("\n");
        for (int i=0;i<4;++i) {
            int index = message.indexOf(lines[i]);
            message.delete(index, index+lines[i].length()+1);
        }
        for (int i=4;i<lines.length-2;++i) {
            int index = lines[i].indexOf(srcFileSuffix());
            String part = lines[i].substring(0,index+srcFileSuffix().length());
            message.delete(message.indexOf(part), message.indexOf(part)+part.length());
        }
        for (int i=lines.length-2;i<lines.length;++i) {
            int index = message.indexOf(lines[i]);
            message.delete(index, index+lines[i].length()+1);
        }
    }*/
    /*protected void processCompileMessage() { // depend on g++ output
        String lines[] = message.toString().split(":");
        ArrayList<String> array = new ArrayList<String>();
        for (int i=0;i<lines.length;++i) {
            String sublines[] = lines[i].split("\n");
            for (int j=0;j<sublines.length;++j)
                array.add(sublines[j]);
        }
        for (int i=0;i<array.size();++i) {
            String cur = array.get(i);
            if (cur.endsWith(srcFileSuffix())) {
                int index = message.indexOf(cur);
                message.delete(index, index+cur.length());
            }
        }
    }*/
    public static void main(String argv[]) {
        if (Configuration.loadFromFile("testserv.cfg.xml")!=0) {
            System.err.println("Configuration file not found or parse error");
            return ;
        }
        String url="jdbc:mysql://localhost/moodle",
            user="moodleuser",
            password="moo";
        Connection connection = null;
        Program p = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            connection.setTransactionIsolation(connection.TRANSACTION_READ_COMMITTED);
            System.out.println("URL: "+url);
            System.out.println("Connection: "+connection);
            //Content.loadAll(connection);
            Problems.getInstance().connection=connection;
            //p = new Program(0, "#include <stdio.h>\n"+
              //  "main() {sdf printf(\"hello world\"); return 0; }\n", Problems.getInstance().getProblemById(1));
            p = new Program(1, "const nmax=1000;\n"+
                    "var mass:array[1..nmax]of integer;\n"+
                    "i,j,m,n:integer;\n"+
                    "begin\n"+
                    "read(n,m);\n"+
                    "for i:=1 to n do\n"+
                    "mass[i]:=i;\n"+
                    "i:=1;\n"+
                    "while n>1 do begin\n"+
                    "i:=(i+m-1) mod n;\n"+
                    "if i=0 then i:=n;\n"+
                    "for j:=i to n-1 do\n"+
                    "mass[j]:=mass[j+1];\n"+
                    "n:=n-1;\n"+
                    "end;\n"+
                    "writeln(mass[1]);\n"+
                    "end.\n"/*"var a: ^Integer; begin new(a);dispose(a);dispose(a);end."*/, Problems.getInstance().getProblemById(6));
            p.prepare();
            Compiler c = CompilerFactory.getInstance().getCompiler(p.lang);
            c.compile(p);
            System.err.println("success compilation");
            InputGenerator inputGenerator = new InputFromFileGenerator(p);
            InputDataProcessor inputDataProcessor = new SimpleInputDataProcessor();
            OutputDataProcessor outputDataProcessor = new SimpleOutputDataProcessor();
            ProgramTester tester = new ProgramTester(inputGenerator,
                    inputDataProcessor, outputDataProcessor);
            tester.execute(p);
        } catch (ClassNotFoundException e) {
            System.err.println("Mysql lib not found: "+e);
        } catch (SQLException e) {
            System.err.println("SQL error occurs: "+e);
        } catch (CompilationInternalServerErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program compilation: "+e.getMessage());
        } catch (TestingInternalServerErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program testing: "+e.getMessage());
        } catch (CompilationErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.COMPILATION_ERROR));
            System.err.println(e.getMessage());
            System.err.println("Compilation error: "+e.getMessage());
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
            System.err.println("Error while program temp directory creation: "+e.getMessage());
        } catch (CanNotCreateTemporaryFileException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program temp file creation: "+e.getMessage());
        } catch (CanNotWriteFileException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.INTERNAL_ERROR));
            System.err.println("Error while program file writing: "+e.getMessage());
        } catch (UnsuccessException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.UNSUCCESS));
            System.err.println("Failed tests: " + e.getMessage());
        } catch (RunTimeErrorException e) {
            System.err.println(ExitCodes.getMsg(ExitCodes.RUNTIME_ERROR));
            System.err.println("Failed tests: " + e.getMessage());
        } finally {
            if (p != null) p.close();
            try {
                if (connection!=null && !connection.isClosed()) connection.close();
            } catch (SQLException e) {
                System.err.println("Error while closing connection: "+e);
            }
        }
    }
}