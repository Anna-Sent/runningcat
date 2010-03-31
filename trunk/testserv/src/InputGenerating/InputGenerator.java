/*
 */
package InputGenerating;

import InputGenerating.Exceptions.InputGeneratingException;
import java.io.Reader;

/**
 *
 * @author partizanka
 */
public abstract class InputGenerator {

    public abstract Reader getReader(int testNumber) throws InputGeneratingException;
}
