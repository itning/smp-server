package top.itning.smp.smpleave.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpleave.entity.Leave;


/**
 * @author itning
 */
public interface LeaveDao extends JpaRepository<Leave, String> {
    /**
     * 获取请假信息
     *
     * @param status   审核状态
     * @param pageable 分页
     * @return 请假信息
     */
    Page<Leave> findAllByStatus(boolean status, Pageable pageable);
}
