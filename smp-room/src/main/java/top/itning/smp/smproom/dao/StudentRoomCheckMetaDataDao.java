package top.itning.smp.smproom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smproom.entity.StudentRoomCheckMetaData;

/**
 * @author itning
 */
public interface StudentRoomCheckMetaDataDao extends JpaRepository<StudentRoomCheckMetaData, String> {
    /**
     * 根据导员ID和KEY查询元数据
     *
     * @param key               KEY
     * @param belongCounselorId 导员ID
     * @return StudentRoomCheckMetaData
     */
    StudentRoomCheckMetaData findByKeyAndBelongCounselorId(String key, String belongCounselorId);

    /**
     * 某个KEY是否存在
     *
     * @param key               KEY
     * @param belongCounselorId 导员ID
     * @return 存在返回<code>true</code>
     */
    boolean existsByKeyAndBelongCounselorId(String key, String belongCounselorId);
}
