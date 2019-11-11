package top.itning.smp.smpinfo.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class FileException extends BaseException {
    public FileException(String msg, HttpStatus httpStatus) {
        super(msg, httpStatus);
    }
}
