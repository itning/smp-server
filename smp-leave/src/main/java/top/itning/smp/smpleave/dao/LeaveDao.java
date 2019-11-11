package top.itning.smp.smpleave.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpleave.entity.Leave;


/**
 * @author itning
 */
public interface LeaveDao extends JpaRepository<Leave, String> {
}
