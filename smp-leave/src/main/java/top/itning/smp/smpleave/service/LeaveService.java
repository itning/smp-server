package top.itning.smp.smpleave.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpleave.dto.LeaveDTO;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.LeaveReason;
import top.itning.smp.smpleave.entity.LeaveType;
import top.itning.smp.smpleave.security.LoginUser;

import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
public interface LeaveService {
    /**
     * 获取请假信息
     *
     * @param pageable 分页信息
     * @param status   状态
     * @return 请假信息集合
     */
    Page<LeaveDTO> getLeaves(Pageable pageable, Boolean status);

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
     * @param pageable  分页
     * @param status    审批状态
     * @return 请假信息集合
     */
    Page<LeaveDTO> search(SearchDTO searchDTO, Pageable pageable, Boolean status);

    /**
     * 新增评论
     *
     * @param leaveId   请假ID
     * @param comment   评论
     * @param loginUser 登录用户
     * @return 新增的评论
     */
    LeaveReason newComment(String leaveId, String comment, LoginUser loginUser);

    /**
     * 审核通过
     *
     * @param leaveId 请假ID
     * @param status  状态
     */
    void leaveCheckStatusChange(String leaveId, boolean status);

    /**
     * 获取正在生效的请假信息数量
     *
     * @param date 目前日期
     * @return 正在生效的请假信息数量
     */
    long countInEffectLeaves(Date date);

    /**
     * 学生今天是否请假了
     *
     * @param userName  学生
     * @param leaveType 请假类型 只能传课假或寝室假，默认包括全部假
     * @return 今天请假了返回<code>true</code>
     */
    boolean isUserLeaveToday(String userName, LeaveType leaveType);

    /**
     * 获取所有请假信息
     *
     * @param whereDay 哪天
     * @return 所有请假信息
     */
    List<LeaveDTO> getLeaves(Date whereDay);
}
