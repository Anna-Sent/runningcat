package IOGenerating;

import IOGenerating.Exceptions.OutputGeneratingException;
import java.io.Reader;

/**
 *
 * @author Анна
 */
public abstract class OutputGenerator {

    /**
     *
     * @param testNumber
     * @return
     * @throws OutputGeneratingException
     */
    public abstract Reader getReader(int testNumber) throws OutputGeneratingException;
}
