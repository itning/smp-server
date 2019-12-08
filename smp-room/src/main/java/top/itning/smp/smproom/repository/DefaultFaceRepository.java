package top.itning.smp.smproom.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.util.StreamUtils;
import top.itning.smp.smproom.cache.FaceImgCache;
import top.itning.smp.smproom.config.CustomProperties;
import top.itning.smp.smproom.entity.Face;

import java.io.File;
import java.io.FileOutputStream;
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
    public Face save(Face entity) {
        String newFileName = customProperties.getFaceLocation() + entity.getId();
        try (FileOutputStream fileOutputStream = new FileOutputStream(newFileName)) {
            StreamUtils.copy(entity.getInputStream(), fileOutputStream);
            // refresh cache
            faceImgCache.refreshStudentFace(entity.getId());
            return entity;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteById(@NonNull String id) {
        faceImgCache.getLoadingCache().invalidate(id);
        new File(customProperties.getFaceLocation() + id).delete();
    }
}
