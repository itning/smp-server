package top.itning.smp.smpclass.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheck;
import top.itning.smp.smpclass.entity.User;


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
}
