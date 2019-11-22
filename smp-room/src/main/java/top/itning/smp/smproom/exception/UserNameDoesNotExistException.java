package top.itning.smp.smproom.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class UserNameDoesNotExistException extends BaseException {
    public UserNameDoesNotExistException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
