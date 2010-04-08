package IOGenerating;

import IOGenerating.Exceptions.InputGeneratingException;
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

    /**
     *
     * @param program
     */
    public InputFromFileGenerator(Program program) {
        this.program = program;
    }

    /**
     *
     * @param testNumber
     * @return
     * @throws InputGeneratingException
     */
    public Reader getReader(int testNumber) throws InputGeneratingException {
        Reader reader = null;
        try {
            reader = new FileReader(program.problem.getAbsPathToTests() + "/" + program.problem.in[testNumber]);
        } catch (FileNotFoundException e) {
            throw new InputGeneratingException("Test input not found: " + e);
        }
        return reader;
    }
}
