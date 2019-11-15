package top.itning.smp.smproom.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smproom.entity.StudentRoomCheck;

/**
 * @author itning
 */
public interface StudentRoomCheckDao extends JpaRepository<StudentRoomCheck, String> {
}
