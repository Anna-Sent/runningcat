package Shared;

/**
 *
 * @author partizanka
 */
public class ExitCodes {

    /**
     * Вывод программы не совпал с эталонным на одном из тестов.
     */
    public static final int UNSUCCESS = 0;
    /**
     * Все тесты успешно выполнены.
     */
    public static final int SUCCESS = 1;
    /**
     * Ошибка компиляции.
     */
    public static final int COMPILATION_ERROR = 2;
    /**
     * Ошибка на сервере.
     */
    public static final int INTERNAL_ERROR = 3;
    /**
     * Программа превысила лимит времени.
     */
    public static final int TIME_OUT_ERROR = 4;
    /**
     * Программа превысила лимит памяти.
     */
    public static final int MEMORY_OUT_ERROR = 5;
    /**
     * Ошибка времени выполнения.
     */
    public static final int RUNTIME_ERROR = 6;

    /**
     * 
     * @param code
     * @return
     */
    public static String getShortMsg(int code) {
        switch (code) {
            case COMPILATION_ERROR:
                return "compilationerror";
            case INTERNAL_ERROR:
                return "internalerror";
            case MEMORY_OUT_ERROR:
                return "memoryout";
            case RUNTIME_ERROR:
                return "runtimeerror";
            case SUCCESS:
                ;
                return "success";
            case TIME_OUT_ERROR:
                return "timeout";
            case UNSUCCESS:
                return "unsuccess";
            default:
                return "";
        }
    }

    /**
     *
     * @param code
     * @return
     */
    public static String getMsg(int code) {
        switch (code) {
            case COMPILATION_ERROR:
                return "Compilation error.";
            case INTERNAL_ERROR:
                return "Internal server error.";
            case MEMORY_OUT_ERROR:
                return "Memory limit exceeded.";
            case RUNTIME_ERROR:
                return "Run time error.";
            case SUCCESS:
                ;
                return "Passed tests.";
            case TIME_OUT_ERROR:
                return "Time limit exceeded.";
            case UNSUCCESS:
                return "Failed tests.";
            default:
                return "";
        }
    }
}
