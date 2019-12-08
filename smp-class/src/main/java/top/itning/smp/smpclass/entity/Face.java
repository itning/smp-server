package top.itning.smp.smpclass.entity;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class Face implements Serializable {
    private String id;
    private BufferedImage bufferedImage;
    private InputStream inputStream;
}
