package top.itning.smp.smpclass.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheckMetaData;

import java.util.List;

/**
 * @author itning
 */
public interface StudentClassCheckMetaDataDao extends JpaRepository<StudentClassCheckMetaData, String>, JpaSpecificationExecutor<StudentClassCheckMetaData> {
    /**
     * 查找最新的教师发起的签到信息
     *
     * @param studentClass 班级
     * @return 最新签到信息
     */
    StudentClassCheckMetaData findTopByStudentClassOrderByGmtCreateDesc(StudentClass studentClass);

    /**
     * 根据班级删除元数据
     *
     * @param studentClass 班级
     */
    void deleteAllByStudentClass(StudentClass studentClass);

    /**
     * 查找所有签到元数据
     *
     * @param studentClass 班级
     * @param pageable     分页
     * @return 所有签到元数据
     */
    Page<StudentClassCheckMetaData> findAllByStudentClass(StudentClass studentClass, Pageable pageable);

    /**
     * 查找所有签到元数据
     *
     * @param studentClass 班级
     * @return 所有签到元数据
     */
    List<StudentClassCheckMetaData> findAllByStudentClass(StudentClass studentClass);
}
