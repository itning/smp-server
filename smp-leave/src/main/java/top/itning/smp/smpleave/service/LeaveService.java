package top.itning.smp.smpleave.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpleave.dto.LeaveDTO;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.security.LoginUser;

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
    Page<LeaveDTO> getLeaves(Pageable pageable);

    /**
     * 新增请假信息
     *
     * @param leave     请假信息
     * @param loginUser 登录用户
     * @return 新增的请假信息
     */
    Leave newLeave(Leave leave, LoginUser loginUser);

    /**
     * 搜索
     *
     * @param searchDTO 关键字
     * @return 请假信息集合
     */
    Page<LeaveDTO> search(SearchDTO searchDTO);
}
