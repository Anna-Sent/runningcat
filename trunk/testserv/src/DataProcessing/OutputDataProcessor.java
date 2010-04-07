/*
 */
package DataProcessing;

import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.Exceptions.OutputTestReadException;
import java.io.BufferedReader;
import java.util.ArrayList;

/**
 *
 * @author partizanka
 */
public abstract class OutputDataProcessor extends DataProcessor {

    ArrayList<String> outputLines;

    /**
     *
     * @return
     */
    public StringBuffer getOutput() {
        StringBuffer sbuf = new StringBuffer();
        for (int i = 0; i < outputLines.size(); ++i) {
            sbuf.append(outputLines.get(i) + "\n");
        }
        return sbuf;
    }

    /**
     *
     * @param outputReader
     * @param testOutputReader
     * @throws OutputTestReadException
     * @throws OutputReadException
     * @throws ComparisonFailedException
     */
    public abstract void process(BufferedReader outputReader,
            BufferedReader testOutputReader)
            throws OutputTestReadException, OutputReadException, ComparisonFailedException;
    //abstract void read();
    //abstract void validate();
    //abstract void compare();
}
