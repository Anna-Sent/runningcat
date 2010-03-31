/*
 */
package DataProcessing;

import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.Exceptions.OutputTestReadException;
import java.io.BufferedReader;

/**
 *
 * @author partizanka
 */
public abstract class OutputDataProcessor extends DataProcessor {

    public abstract void process(BufferedReader outputReader,
            BufferedReader testOutputReader)
            throws OutputTestReadException, OutputReadException, ComparisonFailedException;
    //abstract void read();
    //abstract void validate();
    //abstract void compare();
}
