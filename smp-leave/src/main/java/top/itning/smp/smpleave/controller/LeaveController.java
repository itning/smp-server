package top.itning.smp.smpleave.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.RestModel;
import top.itning.smp.smpleave.service.LeaveService;

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

    /**
     * 获取请假信息
     *
     * @param pageable 分页信息
     * @return ResponseEntity
     */
    @GetMapping("/leaves")
    public ResponseEntity<?> allLeaves(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                               Pageable pageable) {
        return RestModel.ok(leaveService.getLeaves(pageable));
    }

    @PostMapping("/leave")
    public ResponseEntity<?> newLeave(Leave leave) {
        return RestModel.ok(leaveService.newLeave(leave));
    }
}
