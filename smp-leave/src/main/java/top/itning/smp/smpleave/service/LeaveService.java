package top.itning.smp.smpleave.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
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
     * @param pageable  分页信息
     * @param status    状态
     * @param loginUser 登录用户
     * @return 请假信息集合
     */
    Page<LeaveDTO> getLeaves(Pageable pageable, Boolean status, LoginUser loginUser);

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
     * @param loginUser 登录用户
     * @return 请假信息集合
     */
    Page<LeaveDTO> search(SearchDTO searchDTO, Pageable pageable, Boolean status, LoginUser loginUser);

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
     * 更改审批状态
     *
     * @param leaveId   请假ID
     * @param status    状态
     * @param loginUser 登录用户
     */
    void leaveCheckStatusChange(String leaveId, boolean status, LoginUser loginUser);

    /**
     * 获取正在生效的请假信息数量
     *
     * @param date     目前日期
     * @param username 导员用户名
     * @return 正在生效的请假信息数量
     */
    long countInEffectLeaves(Date date, String username);

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
     * @param username 导员用户名，可能为空；空的话全查
     * @return 所有请假信息
     */
    List<LeaveDTO> getLeaves(Date whereDay, @Nullable String username);

    /**
     * 分页获取学生的请假信息
     *
     * @param pageable  分页
     * @param loginUser 登录用户
     * @return 请假信息
     */
    Page<LeaveDTO> getStudentLeaves(Pageable pageable, LoginUser loginUser);

    /**
     * 计算请假人数
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param username  导员用户名
     * @return 人数
     */
    long countAllLeave(Date startDate, Date endDate, String username);
}
