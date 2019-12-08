package top.itning.smp.smproom.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import top.itning.smp.smproom.config.CustomProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 学生注册的人脸图片缓存
 *
 * @author itning
 */
@Component
public class FaceImgCache {
    private final CustomProperties customProperties;
    private final LoadingCache<String, BufferedImage> faceImgCache;

    @Autowired
    public FaceImgCache(CustomProperties customProperties) {
        this.customProperties = customProperties;
        faceImgCache = CacheBuilder
                .newBuilder()
                .maximumSize(100)
                .expireAfterAccess(30, TimeUnit.MINUTES)
                .weakValues()
                .build(new CacheLoader<String, BufferedImage>() {
                    @Override
                    public BufferedImage load(@NonNull String key) throws Exception {
                        return getBufferedImageFromFileName(key);
                    }
                });
    }

    private BufferedImage getBufferedImageFromFileName(@NonNull String fileName) throws IOException {
        File file = new File(customProperties.getFaceLocation() + fileName);
        return ImageIO.read(file);
    }

    @NonNull
    public BufferedImage getBufferedImageFromStudentId(@NonNull String id) {
        return faceImgCache.getUnchecked(id);
    }

    @NonNull
    public LoadingCache<String, BufferedImage> getLoadingCache() {
        return faceImgCache;
    }

    public void refreshStudentFace(@NonNull String id) {
        faceImgCache.refresh(id);
    }
}
