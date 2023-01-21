package cn.edu.hqu.databackup.common.exception;

/**
 * @author egret
 */
public class StatusSystemErrorException extends Exception {

    public StatusSystemErrorException() {
    }

    public StatusSystemErrorException(String message) {
        super(message);
    }

    public StatusSystemErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StatusSystemErrorException(Throwable cause) {
        super(cause);
    }

    public StatusSystemErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}