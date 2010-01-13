/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Compilers;

import Compilers.Exceptions.CompilationInternalServerErrorException;
import Shared.Configuration;
import java.util.HashMap;

/**
 *
 * @author Анна
 */
public class CompilerFactory {
    private static HashMap<Integer, Compiler> compilers = new HashMap<Integer, Compiler>();
    private static CompilerFactory factory = null;
    private CompilerFactory() {
        Integer[] keys = Configuration.getLangs();
        for (int i=0;i<keys.length;++i) {
            compilers.put(keys[i], new Compiler(keys[i]));
        }
    }
    public static CompilerFactory getInstance() {
        return (factory==null)?(factory=new CompilerFactory()):factory;
    }
    public Compiler getCompiler(int lang) throws CompilationInternalServerErrorException {
        Compiler c = compilers.get(new Integer(lang));
        if (c!=null)
            return c;
        else
            throw new CompilationInternalServerErrorException("Compiler with code "+lang+" not found");
    }
}
