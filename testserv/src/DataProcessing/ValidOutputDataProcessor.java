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

    private final Program p;

    /**
     *
     * @param p
     */
    public ValidOutputDataProcessor(Program p) {
        this.p = p;
    }

    @Override
    public StringBuffer getOutput() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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
        //
    }
}
