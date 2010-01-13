/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package InputGenerating;

import InputGenerating.Exceptions.InputGeneratingException;
import Program.Program;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 *
 * @author partizanka
 */
public class InputFromFileGenerator extends InputGenerator {
    private Program program;
    public InputFromFileGenerator(Program program) {
        this.program=program;
    }
    
    public Reader getReader(int testNumber) throws InputGeneratingException {
        Reader reader = null;
        try {
            reader = new FileReader(program.problem.getAbsPathToTests() + "/" + program.problem.in[testNumber]);
        } catch (FileNotFoundException e) {
            throw new InputGeneratingException("Test not found: "+e);
        }
        return reader;
    }
}
