package top.itning.smp.smpleave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.RestModel;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.security.MustCounselorLogin;
import top.itning.smp.smpleave.security.MustLogin;
import top.itning.smp.smpleave.security.MustStudentLogin;
import top.itning.smp.smpleave.service.LeaveService;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        return RestModel.ok(leaveService.getLeaves(pageable, true));
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
        return RestModel.ok(leaveService.getLeaves(pageable, false));
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
                                        @MustLogin LoginUser loginUser) {
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
        return RestModel.ok(leaveService.search(searchDTO, pageable, false));
    }

    /**
     * 审批通过
     *
     * @param leaveId 请假ID
     * @return ResponseEntity
     */
    @PostMapping("/leave/status/true")
    public ResponseEntity<?> leaveCheckStatusChangeTrue(@RequestParam String leaveId,
                                                        @MustCounselorLogin LoginUser loginUser) {
        leaveService.leaveCheckStatusChangeTrue(leaveId);
        return RestModel.noContent();
    }

    /**
     * 新增请假信息
     *
     * @param leave 请假信息
     * @return ResponseEntity
     */
    @PostMapping("/leave")
    public ResponseEntity<?> newLeave(Leave leave, @MustStudentLogin LoginUser loginUser) {
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
        return RestModel.ok(leaveService.search(searchDTO, pageable, true));
    }
}
