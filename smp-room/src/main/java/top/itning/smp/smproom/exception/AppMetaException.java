package top.itning.smp.smproom.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class AppMetaException extends BaseException {
    public AppMetaException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
