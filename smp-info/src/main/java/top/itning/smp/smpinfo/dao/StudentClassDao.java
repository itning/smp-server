package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.StudentClass;

/**
 * @author itning
 */
public interface StudentClassDao extends JpaRepository<StudentClass, String> {
}
