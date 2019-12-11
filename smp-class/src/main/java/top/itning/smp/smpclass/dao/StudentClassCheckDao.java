package top.itning.smp.smpclass.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheck;
import top.itning.smp.smpclass.entity.StudentClassCheckMetaData;
import top.itning.smp.smpclass.entity.User;

import java.util.List;


/**
 * @author itning
 */
public interface StudentClassCheckDao extends JpaRepository<StudentClassCheck, String> {
    /**
     * 根据用户和学生班级查询签到信息
     *
     * @param user         学生
     * @param studentClass 学生班级
     * @param pageable     分页
     * @return 签到信息
     */
    Page<StudentClassCheck> findAllByUserAndStudentClass(User user, StudentClass studentClass, Pageable pageable);

    /**
     * 检查学生是否签到了
     *
     * @param user                      学生
     * @param studentClassCheckMetaData 签到元数据
     * @return 签到了返回<code>true</code>
     */
    boolean existsByUserAndStudentClassCheckMetaData(User user, StudentClassCheckMetaData studentClassCheckMetaData);

    /**
     * 根据班级删除所有打卡信息
     *
     * @param studentClass 班级
     */
    void deleteAllByStudentClass(StudentClass studentClass);

    /**
     * 根据元数据查找签到信息
     *
     * @param studentClassCheckMetaData 元数据
     * @return 签到信息
     */
    List<StudentClassCheck> findAllByStudentClassCheckMetaData(StudentClassCheckMetaData studentClassCheckMetaData);

    /**
     * 查找学生签到
     *
     * @param user                      学生
     * @param studentClass              班级
     * @param studentClassCheckMetaData 元数据
     * @return 学生签到
     */
    StudentClassCheck findTopByUserAndStudentClassAndStudentClassCheckMetaData(User user, StudentClass studentClass, StudentClassCheckMetaData studentClassCheckMetaData);

    /**
     * 根据元数据查找签到信息数量
     *
     * @param studentClassCheckMetaData 元数据
     * @return 数量
     */
    long countAllByStudentClassCheckMetaData(StudentClassCheckMetaData studentClassCheckMetaData);

    /**
     * 根据学生删除签到信息
     *
     * @param user 学生
     */
    void deleteAllByUser(User user);
}
