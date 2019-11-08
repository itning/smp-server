package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.Leave;

/**
 * @author itning
 */
public interface LeaveDao extends JpaRepository<Leave, String> {
}
