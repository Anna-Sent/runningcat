package DataProcessing.Exceptions;

import ProgramTesting.Exceptions.TestingInternalServerErrorException;

/**
 *
 * @author Анна
 */
public class TestReadException extends TestingInternalServerErrorException {

    /**
     * 
     * @param msg
     */
    public TestReadException(String msg) {
        super(msg);
    }
}
