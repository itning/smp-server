package top.itning.smp.smpleave.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpleave.dao.LeaveDao;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.service.LeaveService;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveServiceImpl implements LeaveService {
    private final LeaveDao leaveDao;

    @Autowired
    public LeaveServiceImpl(LeaveDao leaveDao) {
        this.leaveDao = leaveDao;
    }

    @Override
    public Page<Leave> getLeaves(Pageable pageable) {
        return leaveDao.findAll(pageable);
    }

    @Override
    public Leave newLeave(Leave leave) {
        // TODO 设置信息
        return null;
    }
}
