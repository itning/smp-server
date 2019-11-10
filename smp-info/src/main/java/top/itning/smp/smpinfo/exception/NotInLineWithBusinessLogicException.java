package top.itning.smp.smpinfo.exception;

import org.springframework.http.HttpStatus;

/**
 * 不符合业务逻辑
 *
 * @author itning
 */
public class NotInLineWithBusinessLogicException extends BaseException {
    public NotInLineWithBusinessLogicException(String msg, HttpStatus code) {
        super(msg, code);
    }
}
