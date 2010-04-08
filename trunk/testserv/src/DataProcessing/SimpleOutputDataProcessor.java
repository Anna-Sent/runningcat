package DataProcessing;

import DataProcessing.Exceptions.DataReadException;
import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.Exceptions.OutputTestReadException;
import FileOperations.FileOperator;
import java.io.BufferedReader;
import java.util.ArrayList;

/**
 *
 * @author partizanka
 */
public class SimpleOutputDataProcessor extends OutputDataProcessor {

    /**
     *
     * @param outputReader
     * @param testOutputReader
     * @throws OutputReadException
     * @throws OutputTestReadException
     * @throws ComparisonFailedException
     */
    public void process(BufferedReader outputReader, BufferedReader testOutputReader)
            throws OutputReadException, OutputTestReadException, ComparisonFailedException {
        try {
            ArrayList<String> testOutputLines, outputLines;
            try {
                testOutputLines = super.read(testOutputReader);
            } catch (DataReadException e) {
                throw new OutputTestReadException("Test output read error: " + e);
            }
            try {
                outputLines = super.read(outputReader);
                if (!super.isEqual(testOutputLines, outputLines)) {
                    throw new ComparisonFailedException("Test output is not equal to program output");
                }
            } catch (DataReadException e) {
                throw new OutputReadException("Program output read error: " + e);
            }
        } finally {
            FileOperator.close(outputReader);
            FileOperator.close(testOutputReader);
        }
    }
}
