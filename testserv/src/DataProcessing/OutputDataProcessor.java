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

    /**
     * 
     * @return
     */
    public abstract StringBuffer getOutput();

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
