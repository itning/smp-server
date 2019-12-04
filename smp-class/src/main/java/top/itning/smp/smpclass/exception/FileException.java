package top.itning.smp.smpclass.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class FileException extends BaseException {
    public FileException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
