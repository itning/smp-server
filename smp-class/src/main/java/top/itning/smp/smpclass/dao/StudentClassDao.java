package top.itning.smp.smpclass.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.User;

import java.util.Optional;


/**
 * @author itning
 */
public interface StudentClassDao extends JpaRepository<StudentClass, String> {
    /**
     * 根据班号获取班级
     *
     * @param classNum 班号
     * @return 班级
     */
    Optional<StudentClass> findByClassNum(String classNum);

    /**
     * 根据教师查找创建的班级
     *
     * @param user     用户
     * @param pageable 分页
     * @return 教师所有创建的班级
     */
    Page<StudentClass> findByUser(User user, Pageable pageable);
}
