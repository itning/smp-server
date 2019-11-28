package top.itning.smp.smpclass.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class UnexpectedException extends BaseException {
    public UnexpectedException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
