package top.itning.smp.smproom.repository;

import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;
import top.itning.smp.smproom.cache.FaceImgCache;
import top.itning.smp.smproom.config.CustomProperties;
import top.itning.smp.smproom.entity.Face;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
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

    @SneakyThrows
    @Override
    public void deleteAll() {
        faceImgCache.getLoadingCache().invalidateAll();
        FileUtils.cleanDirectory(faceDir);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void deleteAll(@NonNull Iterable<? extends Face> entities) {
        final LoadingCache<String, BufferedImage> loadingCache = faceImgCache.getLoadingCache();
        loadingCache.invalidateAll();
        entities.forEach(face -> {
            loadingCache.invalidate(face.getId());
            File file = new File(customProperties.getFaceLocation() + face.getId());
            if (file.exists()) {
                file.delete();
            }
        });
    }

    @Override
    public void delete(Face entity) {
        this.deleteById(entity.getId());
    }

    @NonNull
    @Override
    public Iterable<Face> saveAll(@NonNull Iterable<Face> entities) {
        List<Face> faces = new ArrayList<>();
        for (Face nextFace : entities) {
            String newFileName = customProperties.getFaceLocation() + nextFace.getId();
            try (FileOutputStream fileOutputStream = new FileOutputStream(newFileName)) {
                StreamUtils.copy(nextFace.getInputStream(), fileOutputStream);
                faces.add(nextFace);
                faceImgCache.refreshStudentFace(nextFace.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return new DefaultIterable<>(faces);
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
