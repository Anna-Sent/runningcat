/*
 */
package ProgramTesting;

import ProgramTesting.Exception.UnsuccessException;
import ProgramTesting.Exception.RunTimeErrorException;
import Program.Program;
import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.InputTestReadException;
import DataProcessing.Exceptions.InputWriteException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.Exceptions.OutputTestReadException;
import DataProcessing.InputDataProcessor;
import DataProcessing.OutputDataProcessor;
import FileOperations.FileOperator;
import InputGenerating.Exceptions.InputGeneratingException;
import InputGenerating.InputGenerator;
import ProcessExecuting.Exceptions.ProcessExecutingException;
import ProcessExecuting.ProcessExecutor;
import ProcessExecuting.Exceptions.ProcessNotRunningException;
import ProgramTesting.Exception.TestingInternalServerErrorException;
import ProgramTesting.Exception.TestingTimeLimitExceededException;
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

/**
 *
 * @author partizanka
 */
public class ProgramTester {

    private InputGenerator inputGenerator;
    private InputDataProcessor inputDataProcessor;
    private OutputDataProcessor outputDataProcessor;

    public ProgramTester(InputGenerator inputGenerator,
            InputDataProcessor inputDataProcessor,
            OutputDataProcessor outputDataProcessor) {
        this.inputGenerator = inputGenerator;
        this.inputDataProcessor = inputDataProcessor;
        this.outputDataProcessor = outputDataProcessor;
    }

    /**
     * Executes and tests the program if it has binary file and system tests are
     * defined.
     *
     * TASKS: memory.
     *
     * @param program Program to execute.
     * @return one of {@link ExitCodes} values
     */ // В случае интернал эррор выставить программе статус непроверенной и попробовать схавать ее еще раз?..
    public void execute(Program program) throws UnsuccessException, TestingInternalServerErrorException, RunTimeErrorException, TestingTimeLimitExceededException {
        if (program.canExecute()) {
            if (program.problem != null && program.problem.n > 0) {
                // executing and testing the program
                for (int i = 0; i < program.problem.n; ++i) { // testing the program test by test...
                    testProgram(program, i);
                }
            } else {
                throw new TestingInternalServerErrorException("System tests not found");
            }
        } else {
            throw new TestingInternalServerErrorException("Binary file doesn't exist");
        }
    }

    private void testProgram(Program program, int testNumber) throws UnsuccessException, TestingInternalServerErrorException, RunTimeErrorException, TestingTimeLimitExceededException {
        BufferedWriter inputWriter = null; // writes to program's input
        BufferedReader testInputReader = null; // reads from test file
        BufferedReader outputReader = null; // reads program's output
        BufferedReader testOutputReader = null; // read a correct test output
        ProcessExecutor executor = new ProcessExecutor(program.getExecuteCmd(), program.getDirPath(), 3000);
        try {
            executor.execute();
            inputWriter = new BufferedWriter(new OutputStreamWriter(executor.getOutputStream())); // throws ProcessNotRunningException
            testInputReader = new BufferedReader(inputGenerator.getReader(testNumber));
            inputDataProcessor.process(inputWriter, testInputReader);
            outputReader = new BufferedReader(new InputStreamReader(executor.getInputStream())); // throws ProcessNotRunningException
            testOutputReader = new BufferedReader(new FileReader(program.problem.getAbsPathToTests() + "/" + program.problem.out[testNumber]));
            outputDataProcessor.process(outputReader, testOutputReader);
        } catch (ProcessExecutingException ex) { // from executor.execute()
            throw new TestingInternalServerErrorException("Program running error: " + ex);
        } catch (IOException ex) {
            throw new TestingInternalServerErrorException("Test output not found: " + ex); //UnsuccessException("An I/O error occurs while program testing: " + ex); // absolutely?
        } catch (InputGeneratingException e) {
            throw new TestingInternalServerErrorException("Input generating error: " + e);
        } catch (ComparisonFailedException e) {
            throw new UnsuccessException(e.toString());
        } catch (InputTestReadException e) {
            throw new TestingInternalServerErrorException(e.toString());
        } catch (InputWriteException e) {
            throw new RunTimeErrorException(e.toString());
        } catch (OutputReadException e) {
            throw new RunTimeErrorException(e.toString());
        } catch (OutputTestReadException e) {
            throw new TestingInternalServerErrorException(e.toString());
        } finally {
            try {
                //if (executor.isRunning()) {
                int code = executor.waitForExit();
                System.err.println("Process was running for " + executor.getWorkTime());
                System.err.println("Program in test case " + testNumber + " exited with code " + code);
                if (executor.isOutOfTime()) {
                    throw new TestingTimeLimitExceededException("Program is out of time");
                }
                if (code != 0) {
                    throw new RunTimeErrorException("Program failed with run time error");
                }
                //}
            } catch (ProcessNotRunningException e) { // from executor.waitForExit(); never
                throw new TestingInternalServerErrorException(e.toString());
            } catch (InterruptedException e) {
                throw new TestingInternalServerErrorException("Interrupted: " + e);
            } finally {
                //FileOperator.close(inputWriter);
                //FileOperator.close(testInputReader);
                //FileOperator.close(outputReader);
                //FileOperator.close(testOutputReader);
            }
        }
    }
}
