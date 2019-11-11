package top.itning.smp.smpsecurity.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class TokenException extends BaseException {
    public TokenException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
