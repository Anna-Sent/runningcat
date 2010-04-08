package Shared;

/**
 *
 * @author partizanka
 */
public class TimeLimitExceededException extends Exception {

    /**
     * 
     * @param msg
     */
    public TimeLimitExceededException(String msg) {
        super(msg);
    }
}
