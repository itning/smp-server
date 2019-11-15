package top.itning.smp.smpclass.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentClass;


/**
 * @author itning
 */
public interface StudentClassDao extends JpaRepository<StudentClass, String> {
}
