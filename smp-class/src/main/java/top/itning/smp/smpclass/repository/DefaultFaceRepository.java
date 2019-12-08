package top.itning.smp.smpclass.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import top.itning.smp.smpclass.cache.FaceImgCache;
import top.itning.smp.smpclass.config.CustomProperties;
import top.itning.smp.smpclass.entity.Face;

import java.io.File;
import java.util.Optional;

/**
 * @author itning
 */
@Repository
public class DefaultFaceRepository extends AbstractFaceRepository {

    @Autowired
    public DefaultFaceRepository(CustomProperties customProperties, FaceImgCache faceImgCache) {
        super(customProperties, faceImgCache);
    }

    @NonNull
    @Override
    public Optional<Face> findById(@NonNull String id) {
        File file = new File(customProperties.getFaceLocation() + id);
        if (file.exists()) {
            Face face = new Face();
            face.setId(file.getName());
            face.setBufferedImage(faceImgCache.getBufferedImageFromStudentId(id));
            return Optional.of(face);
        }
        return Optional.empty();
    }
}
