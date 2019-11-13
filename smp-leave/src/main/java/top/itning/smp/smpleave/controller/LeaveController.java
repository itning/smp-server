package top.itning.smp.smpleave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.RestModel;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.security.MustCounselorLogin;
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
        return RestModel.ok(leaveService.getLeaves(pageable));
    }

    /**
     * 新增请假信息
     *
     * @param leave 请假信息
     * @return ResponseEntity
     */
    @PostMapping("/leave")
    public ResponseEntity<?> newLeave(Leave leave, @MustStudentLogin LoginUser loginUser) {
        return RestModel.ok(leaveService.newLeave(leave, loginUser));
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
        return RestModel.ok(leaveService.search(searchDTO, pageable));
    }
}
