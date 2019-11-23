package top.itning.smp.smproom.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class SavedException extends BaseException {
    public SavedException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
