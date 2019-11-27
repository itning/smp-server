package top.itning.smp.smpleave.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class DateRangeException extends BaseException {
    public DateRangeException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
}
