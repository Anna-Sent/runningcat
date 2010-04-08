package Compilers.Exceptions;

import Shared.InternalServerErrorException;

/**
 *
 * @author Анна
 */
public class CompilationInternalServerErrorException extends InternalServerErrorException {

    /**
     *
     * @param msg
     */
    public CompilationInternalServerErrorException(String msg) {
        super(msg);
    }
}
