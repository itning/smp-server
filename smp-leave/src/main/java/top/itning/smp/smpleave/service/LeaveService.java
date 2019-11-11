package top.itning.smp.smpleave.service;

import top.itning.smp.smpleave.entity.Leave;

import java.util.List;

/**
 * @author itning
 */
public interface LeaveService {
    /**
     * 获取请假信息
     *
     * @return 请假信息集合
     */
    List<Leave> getLeaves();
}
