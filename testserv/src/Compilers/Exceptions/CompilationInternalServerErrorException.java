package Compilers.Exceptions;

import Shared.InternalServerErrorException;

/**
 * Класс-исключение "Ошибка на сервере при выполнении компиляции".
 * <p>
 * Может возникнуть в двух случаях:
 * <ul>
 * <li> компилятор не найден по заданному коду языка программирования
 * <li> при выполнении процесса компиляции произошла ошибка (самое частое:
 * не найдена программа-компилятор)
 * </ul>
 *
 * @author Анна
 */
public class CompilationInternalServerErrorException extends InternalServerErrorException {

    /**
     * Конструктор класса.
     *
     * @param msg сообщение об ошибке.
     */
    public CompilationInternalServerErrorException(String msg) {
        super(msg);
    }
}
