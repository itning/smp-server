package top.itning.smp.smpclass.repository;

import org.apache.commons.io.FileUtils;
import org.springframework.lang.NonNull;
import top.itning.smp.smpclass.cache.FaceImgCache;
import top.itning.smp.smpclass.config.CustomProperties;
import top.itning.smp.smpclass.entity.Face;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author itning
 */
public abstract class AbstractFaceRepository implements FaceRepository<Face, String> {
    final CustomProperties customProperties;
    final FaceImgCache faceImgCache;
    final File faceDir;

    public AbstractFaceRepository(CustomProperties customProperties, FaceImgCache faceImgCache) {
        this.customProperties = customProperties;
        this.faceImgCache = faceImgCache;
        this.faceDir = new File(customProperties.getFaceLocation());
    }

    @Override
    public boolean existsById(@NonNull String id) {
        return new File(customProperties.getFaceLocation() + id).exists();
    }

    @NonNull
    @Override
    public Iterable<Face> findAll() {
        List<Face> collect = FileUtils
                .listFiles(faceDir, null, false)
                .stream()
                .map(file -> {
                    Face face = new Face();
                    face.setId(file.getName());
                    face.setBufferedImage(faceImgCache.getBufferedImageFromStudentId(file.getName()));
                    return face;
                })
                .collect(Collectors.toList());
        return new DefaultIterable<>(collect);
    }

    @NonNull
    @Override
    public Iterable<Face> findAllById(@NonNull Iterable<String> iterable) {
        List<Face> faces = new ArrayList<>();
        for (String id : iterable) {
            File file = new File(customProperties.getFaceLocation() + id);
            if (!file.exists()) {
                continue;
            }
            Face face = new Face();
            face.setId(id);
            face.setBufferedImage(faceImgCache.getBufferedImageFromStudentId(file.getName()));
            faces.add(face);
        }
        return new DefaultIterable<>(faces);
    }

    @Override
    public long count() {
        File[] files = faceDir.listFiles(File::isFile);
        return files == null ? 0L : files.length;
    }

    public static class DefaultIterable<S> implements Iterable<S> {
        private final Collection<S> collection;

        public DefaultIterable(Collection<S> collection) {
            this.collection = collection;
        }

        @NonNull
        @Override
        public Iterator<S> iterator() {
            return collection.iterator();
        }
    }
}
