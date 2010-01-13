/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ProgramTesting.Exception;

import Shared.InternalServerErrorException;

/**
 *
 * @author Анна
 */
public class TestingInternalServerErrorException extends InternalServerErrorException {

    public TestingInternalServerErrorException(String msg) {
        super(msg);
    }

}
