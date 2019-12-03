package top.itning.smp.smpleave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpleave.dto.LeaveDTO;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.LeaveType;
import top.itning.smp.smpleave.entity.RestModel;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.security.MustCounselorLogin;
import top.itning.smp.smpleave.security.MustLogin;
import top.itning.smp.smpleave.security.MustStudentLogin;
import top.itning.smp.smpleave.service.LeaveService;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static top.itning.smp.smpleave.security.MustLogin.ROLE.COUNSELOR;
import static top.itning.smp.smpleave.security.MustLogin.ROLE.STUDENT;

/**
 * @author itning
 */
@RestController
public class LeaveController {
    private final LeaveService leaveService;

    @Autowired
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) {
        binder.registerCustomEditor(
                Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    /**
     * 获取请假信息
     *
     * @param pageable 分页信息
     * @return ResponseEntity
     */
    @GetMapping("/leaves")
    public ResponseEntity<?> allLeaves(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                               Pageable pageable,
                                       @MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(leaveService.getLeaves(pageable, true, loginUser));
    }

    /**
     * 获取请假审批信息
     *
     * @param pageable 分页信息
     * @return ResponseEntity
     */
    @GetMapping("/leaves/check")
    public ResponseEntity<?> allCheckLeaves(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                                    Pageable pageable,
                                            @MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(leaveService.getLeaves(pageable, null, loginUser));
    }

    /**
     * 评论
     *
     * @param leaveId 请假ID
     * @param comment 评论
     * @return ResponseEntity
     */
    @PostMapping("/leave/comment")
    public ResponseEntity<?> newComment(@RequestParam String leaveId,
                                        @RequestParam String comment,
                                        @MustLogin(role = {STUDENT, COUNSELOR}) LoginUser loginUser) {
        return RestModel.created(leaveService.newComment(leaveId, comment, loginUser));
    }

    /**
     * 搜索审批
     *
     * @param pageable  分页
     * @param searchDTO 查询条件
     * @return ResponseEntity
     */
    @GetMapping("/search/leaves/check")
    public ResponseEntity<?> searchCheck(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                                 Pageable pageable,
                                         @MustCounselorLogin LoginUser loginUser,
                                         SearchDTO searchDTO) {
        return RestModel.ok(leaveService.search(searchDTO, pageable, null, loginUser));
    }

    /**
     * 更改审批状态
     *
     * @param leaveId 请假ID
     * @return ResponseEntity
     */
    @PostMapping("/leave/status")
    public ResponseEntity<?> leaveCheckStatusChangeTrue(@RequestParam String leaveId,
                                                        @RequestParam boolean status,
                                                        @MustCounselorLogin LoginUser loginUser) {
        leaveService.leaveCheckStatusChange(leaveId, status, loginUser);
        return RestModel.noContent();
    }

    /**
     * 新增请假信息
     *
     * @param startTime 请假开始时间
     * @param endTime   请假结束时间
     * @param reason    原因
     * @param leaveType 请假类型
     * @return 请假信息
     */
    @PostMapping("/leave")
    public ResponseEntity<?> newLeave(@RequestParam Date startTime,
                                      @RequestParam Date endTime,
                                      @RequestParam String reason,
                                      @RequestParam String leaveType,
                                      @MustStudentLogin LoginUser loginUser) {
        Leave leave = new Leave();
        leave.setStartTime(startTime);
        leave.setEndTime(endTime);
        leave.setReason(reason);
        leave.setLeaveType(LeaveType.valueOf(leaveType));
        return RestModel.created(leaveService.newLeave(leave, loginUser));
    }

    /**
     * 搜索
     *
     * @param pageable  分页
     * @param searchDTO 查询条件
     * @return ResponseEntity
     */
    @GetMapping("/search/leaves")
    public ResponseEntity<?> search(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                            Pageable pageable,
                                    @MustCounselorLogin LoginUser loginUser,
                                    SearchDTO searchDTO) {
        return RestModel.ok(leaveService.search(searchDTO, pageable, true, loginUser));
    }

    /**
     * 学生获取请假信息
     *
     * @param pageable 分页
     * @return ResponseEntity
     */
    @GetMapping("/studentLeaves")
    public ResponseEntity<?> getStudentLeaves(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                                      Pageable pageable,
                                              @MustStudentLogin LoginUser loginUser) {
        return RestModel.ok(leaveService.getStudentLeaves(pageable, loginUser));
    }

    /**
     * 获取正在生效的请假信息数量
     *
     * @return 正在生效的请假信息数量
     */
    @GetMapping("/internal/leaves/inEffect/count")
    public long inEffectLeaves(@RequestParam Date date) {
        return leaveService.countInEffectLeaves(date);
    }

    /**
     * 学生今天是否请假了
     *
     * @param userName  学生
     * @param leaveType 请假类型 只能传课假或寝室假，默认包括全部假
     * @return 今天请假了返回<code>true</code>
     */
    @GetMapping("/internal/isLeave")
    public boolean isLeave(@RequestParam("userName") String userName, @RequestParam("leaveType") LeaveType leaveType) {
        return leaveService.isUserLeaveToday(userName, leaveType);
    }

    /**
     * 获取请假信息
     *
     * @param whereDay 哪天
     * @return 所有请假信息
     */
    @GetMapping("/internal/leaves")
    public List<LeaveDTO> getAllLeave(@RequestParam("whereDay") Date whereDay) {
        return leaveService.getLeaves(whereDay);
    }
}
