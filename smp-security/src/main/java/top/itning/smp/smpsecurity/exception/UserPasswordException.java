package top.itning.smp.smpsecurity.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class UserPasswordException extends BaseException {
    public UserPasswordException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
