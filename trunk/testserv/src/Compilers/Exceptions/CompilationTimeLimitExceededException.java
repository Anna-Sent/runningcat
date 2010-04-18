package Compilers.Exceptions;

import Shared.TimeLimitExceededException;

/**
 * Класс-исключение "Процесс компиляции превысил ограничение по времени".
 * Теоретически может возникнуть при компиляции программы на C++.
 * 
 * @author Анна
 */
public class CompilationTimeLimitExceededException extends TimeLimitExceededException {

    /**
     * Конструктор класса.
     *
     * @param msg сообщение об ошибке
     */
    public CompilationTimeLimitExceededException(String msg) {
        super(msg);
    }
}
