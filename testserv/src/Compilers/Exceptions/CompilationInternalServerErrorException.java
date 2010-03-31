/*
 */
package Compilers.Exceptions;

import Shared.InternalServerErrorException;

/**
 *
 * @author Анна
 */
public class CompilationInternalServerErrorException extends InternalServerErrorException {

    public CompilationInternalServerErrorException(String msg) {
        super(msg);
    }
}
