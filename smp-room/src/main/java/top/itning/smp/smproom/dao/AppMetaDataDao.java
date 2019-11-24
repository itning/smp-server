package top.itning.smp.smproom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smproom.entity.AppMetaData;

/**
 * @author itning
 */
public interface AppMetaDataDao extends JpaRepository<AppMetaData, String> {
}
