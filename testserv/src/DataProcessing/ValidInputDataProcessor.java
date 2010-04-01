/*
 */
package DataProcessing;

import Program.Program;
import DataProcessing.Exceptions.InputTestReadException;
import DataProcessing.Exceptions.InputWriteException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 *
 * @author partizanka
 */
public class ValidInputDataProcessor extends InputDataProcessor {
    private final Program p;

    /**
     *
     * @param p
     */
    public ValidInputDataProcessor(Program p) {
        this.p = p;
    }

    /**
     *
     * @param inputWriter
     * @param testInputReader
     * @throws InputTestReadException
     * @throws InputWriteException
     */
    public void process(BufferedWriter inputWriter, BufferedReader testInputReader)
            throws InputTestReadException, InputWriteException {
        //
    }
}
