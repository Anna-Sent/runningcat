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

    /**
     * 
     * @param testNumber
     * @return
     * @throws InputGeneratingException
     */
    public abstract Reader getReader(int testNumber) throws InputGeneratingException;
}
