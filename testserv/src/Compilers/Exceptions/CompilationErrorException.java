package Compilers.Exceptions;

/**
 * Класс-исключение "Ошибка компиляции" возникает, если процесс компиляции
 * завершается с кодом возврата не равным нулю (чаще всего, 1). Это значит,
 * что программа не компилируется.
 *
 * @author partizanka
 */
public class CompilationErrorException extends Exception {

    /**
     * Конструктор класса.
     *
     * @param msg сообщение об ошибке
     */
    public CompilationErrorException(String msg) {
        super(msg);
    }
}
