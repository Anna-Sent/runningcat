/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
