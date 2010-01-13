/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package InputGenerating;

import InputGenerating.Exceptions.InputGeneratingException;
import java.io.Reader;

/**
 *
 * @author partizanka
 */
public abstract class InputGenerator {
    public abstract Reader getReader(int testNumber) throws InputGeneratingException;
}
