/*
 */
package DataProcessing;

import Program.Program;
import DataProcessing.Exceptions.ComparisonFailedException;
import DataProcessing.Exceptions.OutputReadException;
import DataProcessing.Exceptions.OutputTestReadException;
import java.io.BufferedReader;

/**
 *
 * @author partizanka
 */
public class ValidOutputDataProcessor extends OutputDataProcessor {

    public ValidOutputDataProcessor(Program p) {
    }

    public void process(BufferedReader outputReader, BufferedReader testOutputReader)
            throws OutputReadException, OutputTestReadException, ComparisonFailedException {
        //
    }
}
