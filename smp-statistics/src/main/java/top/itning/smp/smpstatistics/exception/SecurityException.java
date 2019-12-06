package top.itning.smp.smpstatistics.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class SecurityException extends BaseException {
    public SecurityException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
