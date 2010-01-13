/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package DataProcessing;

import DataProcessing.Exceptions.DataWriteException;
import DataProcessing.Exceptions.DataReadException;
import DataProcessing.Exceptions.InputTestReadException;
import DataProcessing.Exceptions.InputWriteException;
import FileOperations.FileOperator;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author partizanka
 */
public class SimpleInputDataProcessor extends InputDataProcessor {
    public void process(BufferedWriter inputWriter, BufferedReader testInputReader)
            throws InputTestReadException, InputWriteException {
        ArrayList<String> lines;
        try {
            lines = super.read(testInputReader);
            super.write(inputWriter, lines);
        } catch (DataReadException e) {
            throw new InputTestReadException(e.toString());
        } catch (DataWriteException e) {
            throw new InputWriteException(e.toString());
        } finally {
            FileOperator.close(inputWriter);
            FileOperator.close(testInputReader);
        }
    }
}
