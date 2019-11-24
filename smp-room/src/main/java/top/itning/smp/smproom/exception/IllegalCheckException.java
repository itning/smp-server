package top.itning.smp.smproom.exception;

import org.springframework.http.HttpStatus;

/**
 * 非法签到
 *
 * @author itning
 */
public class IllegalCheckException extends BaseException {
    public IllegalCheckException(String msg) {
        super(msg, HttpStatus.BAD_REQUEST);
    }
}
