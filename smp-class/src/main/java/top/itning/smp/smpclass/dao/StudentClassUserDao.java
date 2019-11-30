package top.itning.smp.smpclass.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.entity.StudentGroupPrimaryKey;
import top.itning.smp.smpclass.entity.User;

import java.util.List;


/**
 * @author itning
 */
public interface StudentClassUserDao extends JpaRepository<StudentClassUser, StudentGroupPrimaryKey> {
    /**
     * 根据用户查找群组
     *
     * @param user     用户
     * @param pageable 分页
     * @return 群组
     */
    Page<StudentClassUser> findAllByUser(User user, Pageable pageable);

    /**
     * 学生是否已经加入了班级
     *
     * @param user         学生
     * @param studentClass 班级
     * @return 学生已经加入了班级返回<code>true</code>
     */
    boolean existsByUserAndStudentClass(User user, StudentClass studentClass);

    /**
     * 查找学生的班级
     *
     * @param user         用户
     * @param studentClass 班级
     * @return 学生加入的班级
     */
    StudentClassUser findByUserAndStudentClass(User user, StudentClass studentClass);

    /**
     * 根据班级删除所有学生加入的数据
     *
     * @param studentClass 班级
     */
    void deleteAllByStudentClass(StudentClass studentClass);

    /**
     * 查找某个班级的所有加入的学生
     *
     * @param studentClass 班级
     * @return 所有学生
     */
    List<StudentClassUser> findAllByStudentClass(StudentClass studentClass);
}
