package ProgramTesting;

import ProgramTesting.Exceptions.UnsuccessException;
import ProgramTesting.Exceptions.RunTimeErrorException;
import Program.Program;
import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.InputTestReadException;
import DataProcessing.Exceptions.InputWriteException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.Exceptions.OutputTestReadException;
import DataProcessing.Exceptions.TestReadException;
import DataProcessing.InputDataProcessor;
import DataProcessing.OutputDataProcessor;
import FileOperations.FileOperator;
import IOGenerating.Exceptions.IOGeneratingException;
import IOGenerating.Exceptions.InputGeneratingException;
import IOGenerating.Exceptions.OutputGeneratingException;
import IOGenerating.InputGenerator;
import IOGenerating.OutputGenerator;
import ProcessExecuting.Exceptions.ProcessExecutingException;
import ProcessExecuting.ProcessExecutor;
import ProcessExecuting.Exceptions.ProcessNotRunningException;
import ProgramTesting.Exceptions.TestingInternalServerErrorException;
import ProgramTesting.Exceptions.TestingTimeLimitExceededException;
import Shared.Configuration;
import Shared.ExitCodes;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;

/**
 *
 * @author partizanka
 */
public class ProgramTester {

    private InputGenerator inputGenerator;
    private OutputGenerator outputGenerator;
    private InputDataProcessor inputDataProcessor;
    private OutputDataProcessor outputDataProcessor;
    private StringBuffer message;

    /**
     *
     * @param inputGenerator
     * @param outputGenerator 
     * @param inputDataProcessor
     * @param outputDataProcessor
     */
    public ProgramTester(InputGenerator inputGenerator,
            OutputGenerator outputGenerator,
            InputDataProcessor inputDataProcessor,
            OutputDataProcessor outputDataProcessor) {
        this.inputGenerator = inputGenerator;
        this.outputGenerator = outputGenerator;
        this.inputDataProcessor = inputDataProcessor;
        this.outputDataProcessor = outputDataProcessor;
    }

    /**
     * Executes and tests the program if it has binary file and system tests are
     * defined.
     *
     * TODO memory
     *
     * @param program Program to execute.
     * @throws UnsuccessException
     * @throws TestingInternalServerErrorException 
     * @throws TestingTimeLimitExceededException
     * @throws RunTimeErrorException
     */
    public void execute(Program program) throws
            UnsuccessException,
            TestingInternalServerErrorException,
            RunTimeErrorException,
            TestingTimeLimitExceededException {
        //if (program.canExecute()) {
        if (program.problem != null && program.problem.n > 0) {
            // executing and testing the program
            for (int i = 0; i < program.problem.n; ++i) { // testing the program test by test...
                testProgram(program, i);
            }
        } else {
            throw new TestingInternalServerErrorException("System tests not found");
        }
        //} else {
        //    throw new TestingInternalServerErrorException("Binary file doesn't exist");
        //}
    }

    private void processMessage(Program p) {
        int index;
        index = message.indexOf(p.getSrcFileName());
        while (index > -1) {
            message = message.replace(index, index + p.getSrcFileName().length(), "&lt;code&gt;");
            index = message.indexOf(p.getSrcFileName());
        }
    }

    private void testProgram(Program program, int testNumber) throws
            UnsuccessException,
            TestingInternalServerErrorException,
            RunTimeErrorException,
            TestingTimeLimitExceededException {
        BufferedWriter inputWriter = null; // writes to program's input
        BufferedReader testInputReader = null; // reads from test file
        BufferedReader outputReader = null; // reads program's output
        BufferedReader testOutputReader = null; // read a correct test output
        BufferedReader errorReader = null;
        ProcessExecutor executor = new ProcessExecutor(program.getExecuteCmd(), program.getDirPath(), 3000);
        try {
            executor.execute();
            try {
                inputWriter = new BufferedWriter(
                        new OutputStreamWriter(executor.getOutputStream())); // throws ProcessNotRunningException
                testInputReader = new BufferedReader(
                        inputGenerator.getReader(testNumber));
                inputDataProcessor.process(inputWriter, testInputReader);

                outputReader = new BufferedReader(
                        new InputStreamReader(executor.getInputStream())); // throws ProcessNotRunningException
                testOutputReader = new BufferedReader(
                        outputGenerator.getReader(testNumber));
                outputDataProcessor.process(outputReader, testOutputReader);
            } catch (InputWriteException e) {
                // Если не работает запись на вход программы или чтение выхода
                // программы, то произошла ошибка времени выполнения.
            } catch (OutputReadException e) {
                // throw new RunTimeErrorException(e.toString());
            }
            message = new StringBuffer();
            try {
                errorReader = new BufferedReader(new InputStreamReader(executor.getErrorStream()));
                String line;
                while ((line = errorReader.readLine()) != null) {
                    message.append(line + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            int code = executor.waitForExit(); // throws ProcessNotRunningException, InterruptedException
            System.err.println("Process was running for " + executor.getWorkTime());
            System.err.println("Program in test case " + testNumber + " exited with code " + code);
            if (executor.isOutOfTime()) {
                throw new TestingTimeLimitExceededException("Program is out of time");
            }
            if (code != 0) {
                processMessage(program);
                throw new RunTimeErrorException(message.toString());
            }
        } catch (ProcessExecutingException ex) { // from executor.execute()
            throw new TestingInternalServerErrorException("Program running error: " + ex);
        } catch (InterruptedException e) {
            throw new TestingInternalServerErrorException("Interrupted: " + e);
        } catch (TestingInternalServerErrorException e) {
            if (executor.quietStop()) {
                System.err.println("Process was running for " + executor.getWorkTime());
                System.err.println("Program in test case " + testNumber + " exited with code " + executor.getCode());
            }
            throw e;
        } catch (ComparisonFailedException e) {
            if (executor.quietStop()) {
                System.err.println("Process was running for " + executor.getWorkTime());
                System.err.println("Program in test case " + testNumber + " exited with code " + executor.getCode());
            }
            throw new UnsuccessException(e.toString());
        } finally {
            FileOperator.close(errorReader);
        }
    }
}
