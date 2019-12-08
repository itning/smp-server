package top.itning.smp.smpclass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author itning
 */
@ConfigurationProperties(prefix = "app")
@Component
public class CustomProperties {
    /**
     * 学生注册的人脸存储路径
     */
    private String faceLocation;
    /**
     * 人脸对比精度阈值
     */
    private float contrastAccuracyThreshold = 0.5f;

    public String getFaceLocation() {
        return faceLocation;
    }

    public void setFaceLocation(String faceLocation) {
        if (!faceLocation.endsWith(File.separator)) {
            faceLocation += File.separator;
        }
        this.faceLocation = faceLocation;
    }

    public float getContrastAccuracyThreshold() {
        return contrastAccuracyThreshold;
    }

    public void setContrastAccuracyThreshold(float contrastAccuracyThreshold) {
        if (contrastAccuracyThreshold > 1) {
            contrastAccuracyThreshold = 1f;
        }
        if (contrastAccuracyThreshold < 0) {
            contrastAccuracyThreshold = 0;
        }
        this.contrastAccuracyThreshold = contrastAccuracyThreshold;
    }
}
