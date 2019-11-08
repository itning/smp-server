package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.StudentRoomCheck;

/**
 * @author itning
 */
public interface StudentRoomCheckDao extends JpaRepository<StudentRoomCheck, String> {
}
