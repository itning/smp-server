package top.itning.smp.smpclass.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;

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
}
