package top.itning.smp.smpleave.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpleave.dao.LeaveDao;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.service.LeaveService;

import java.util.List;

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
    public List<Leave> getLeaves() {
        return leaveDao.findAll();
    }
}
