package IOGenerating.Exceptions;

import ProgramTesting.Exceptions.TestingInternalServerErrorException;

/**
 *
 * @author Анна
 */
public class IOGeneratingException extends TestingInternalServerErrorException {

    /**
     *
     * @param msg
     */
    public IOGeneratingException(String msg) {
        super(msg);
    }
}
