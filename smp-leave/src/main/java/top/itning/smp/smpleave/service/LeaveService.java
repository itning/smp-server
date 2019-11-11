package top.itning.smp.smpleave.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpleave.entity.Leave;

/**
 * @author itning
 */
public interface LeaveService {
    /**
     * 获取请假信息
     *
     * @param pageable 分页信息
     * @return 请假信息集合
     */
    Page<Leave> getLeaves(Pageable pageable);

    /**
     * 新增请假信息
     *
     * @param leave 请假信息
     * @return 新增的请假信息
     */
    Leave newLeave(Leave leave);
}
