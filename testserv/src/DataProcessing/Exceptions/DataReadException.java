/*
 */
package DataProcessing.Exceptions;

/**
 * Data type exception, data validation exception, data read exception.
 * @author partizanka
 */
public class DataReadException extends Exception {

    /**
     *
     * @param msg
     */
    public DataReadException(String msg) {
        super(msg);
    }
}
