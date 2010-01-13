/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
