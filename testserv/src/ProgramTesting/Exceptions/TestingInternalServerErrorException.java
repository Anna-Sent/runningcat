package ProgramTesting.Exceptions;

import Shared.InternalServerErrorException;

/**
 *
 * @author Анна
 */
public class TestingInternalServerErrorException extends InternalServerErrorException {

    /**
     * 
     * @param msg
     */
    public TestingInternalServerErrorException(String msg) {
        super(msg);
    }
}
