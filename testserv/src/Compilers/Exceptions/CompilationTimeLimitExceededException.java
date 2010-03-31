/*
 */
package Compilers.Exceptions;

import Shared.TimeLimitExceededException;

/**
 *
 * @author Анна
 */
public class CompilationTimeLimitExceededException extends TimeLimitExceededException {

    public CompilationTimeLimitExceededException(String msg) {
        super(msg);
    }
}
