package top.itning.smp.smproom.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class FileNotFoundException extends BaseException {
    public FileNotFoundException(String msg) {
        super(msg, HttpStatus.NOT_FOUND);
    }
}
