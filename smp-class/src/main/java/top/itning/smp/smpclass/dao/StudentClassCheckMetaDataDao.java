package top.itning.smp.smpclass.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheckMetaData;

/**
 * @author itning
 */
public interface StudentClassCheckMetaDataDao extends JpaRepository<StudentClassCheckMetaData, String> {
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
}
