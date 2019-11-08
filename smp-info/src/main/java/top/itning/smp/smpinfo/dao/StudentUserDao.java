package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.StudentUser;

/**
 * @author itning
 */
public interface StudentUserDao extends JpaRepository<StudentUser, String> {
}
