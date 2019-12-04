package top.itning.smp.smproom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smproom.entity.StudentRoomCheckMetaData;
import top.itning.smp.smproom.entity.StudentRoomCheckMetaDataPrimaryKey;

/**
 * @author itning
 */
public interface StudentRoomCheckMetaDataDao extends JpaRepository<StudentRoomCheckMetaData, StudentRoomCheckMetaDataPrimaryKey> {
    /**
     * 根据导员ID和KEY查询元数据
     *
     * @param key               KEY
     * @param belongCounselorId 导员ID
     * @return StudentRoomCheckMetaData
     */
    StudentRoomCheckMetaData findByKeyAndBelongCounselorId(String key, String belongCounselorId);
}
