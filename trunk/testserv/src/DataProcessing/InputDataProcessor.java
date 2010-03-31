/*
 */
package DataProcessing;

import DataProcessing.Exceptions.InputTestReadException;
import DataProcessing.Exceptions.InputWriteException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 *
 * @author partizanka
 */
public abstract class InputDataProcessor extends DataProcessor {

    public abstract void process(BufferedWriter inputWriter,
            BufferedReader testInputReader)
            throws InputTestReadException, InputWriteException;
    //abstract void read();
    //abstract void validate();
    //abstract void write(); //?
}
