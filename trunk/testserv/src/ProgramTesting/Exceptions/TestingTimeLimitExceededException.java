/*
 */
package ProgramTesting.Exceptions;

import Shared.TimeLimitExceededException;

/**
 *
 * @author Анна
 */
public class TestingTimeLimitExceededException extends TimeLimitExceededException {

    /**
     * 
     * @param msg
     */
    public TestingTimeLimitExceededException(String msg) {
        super(msg);
    }
}
