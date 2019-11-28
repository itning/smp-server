package top.itning.smp.smpleave.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.User;

import java.util.List;


/**
 * @author itning
 */
public interface LeaveDao extends JpaRepository<Leave, String>, JpaSpecificationExecutor<Leave> {
    /**
     * 获取请假信息
     *
     * @param status   审核状态
     * @param pageable 分页
     * @return 请假信息
     */
    Page<Leave> findAllByStatus(Boolean status, Pageable pageable);

    /**
     * 根据用户寻找请假信息
     *
     * @param user     用户
     * @param pageable 分页
     * @return 请假信息
     */
    Page<Leave> findAllByUser(User user, Pageable pageable);

    /**
     * 根据用户寻找请假信息
     *
     * @param user 用户
     * @return 请假信息
     */
    List<Leave> findAllByUser(User user);
}