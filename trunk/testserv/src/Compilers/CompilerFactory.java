package Compilers;

import Compilers.Exceptions.CompilationInternalServerErrorException;
import Shared.Configuration;
import java.util.HashMap;

/**
 * Класс-синглтон "Фабрика" возвращает компиляторы языков программирования,
 * описанных в файле конфигурации.
 *
 * @author Анна
 */
public class CompilerFactory {

    private static HashMap<Integer, Compiler> compilers = new HashMap<Integer, Compiler>();
    private static CompilerFactory factory = null;

    private CompilerFactory() {
        Integer[] keys = Configuration.getLangs();
        for (int i = 0; i < keys.length; ++i) {
            compilers.put(keys[i], new Compiler(keys[i]));
        }
    }

    /**
     * Возвращает экземпляр класса-фабрики.
     *
     * @return экземпляр класса-фабрики
     */
    public static CompilerFactory getInstance() {
        return (factory == null) ? (factory = new CompilerFactory()) : factory;
    }

    /**
     * Возвращает экземпляр класса компилятора {@link Compiler} для заданного
     * кода языка программирования. До вызова метода должна быть выполнена
     * проверка того, что язык программирования является компилируемым.
     *
     * @param lang код языка программирования
     * @return экземпляр класса компилятора {@code Compiler}
     * @throws CompilationInternalServerErrorException возникает, если
     * язык программирования с данным кодом не был описан в файле конфигурации
     */
    public Compiler getCompiler(int lang) throws CompilationInternalServerErrorException {
        Compiler c = compilers.get(new Integer(lang));
        if (c != null) {
            return c;
        } else {
            throw new CompilationInternalServerErrorException("Compiler with code " + lang + " not found");
        }
    }
}
