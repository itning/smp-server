package top.itning.smp.smpleave.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpleave.entity.LeaveReason;

/**
 * @author itning
 */
public interface LeaveReasonDao extends JpaRepository<LeaveReason, String> {
}
