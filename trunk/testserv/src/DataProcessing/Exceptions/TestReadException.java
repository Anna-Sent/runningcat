package DataProcessing.Exceptions;

import ProgramTesting.Exceptions.TestingInternalServerErrorException;

/**
 *
 * @author Анна
 */
public class TestReadException extends TestingInternalServerErrorException {

    public TestReadException(String msg) {
        super(msg);
    }
}
