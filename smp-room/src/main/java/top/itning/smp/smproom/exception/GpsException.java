package top.itning.smp.smproom.exception;

import org.springframework.http.HttpStatus;

/**
 * @author itning
 */
public class GpsException extends BaseException {
    public GpsException(double longitude, double latitude) {
        super("GPS错误:" + longitude + ";" + latitude, HttpStatus.BAD_REQUEST);
    }
}
