package top.itning.smp.smpleave.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpleave.client.InfoClient;
import top.itning.smp.smpleave.dao.LeaveDao;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.User;
import top.itning.smp.smpleave.exception.UnexpectedException;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.service.LeaveService;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveServiceImpl implements LeaveService {
    private static final Logger logger = LoggerFactory.getLogger(LeaveServiceImpl.class);

    private final LeaveDao leaveDao;
    private final InfoClient infoClient;

    @Autowired
    public LeaveServiceImpl(LeaveDao leaveDao, InfoClient infoClient) {
        this.leaveDao = leaveDao;
        this.infoClient = infoClient;
    }

    @Override
    public Page<Leave> getLeaves(Pageable pageable) {
        return leaveDao.findAll(pageable);
    }

    @Override
    public Leave newLeave(Leave leave, LoginUser loginUser) {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> {
            // 不应出现该异常，因为用户传参必然存在
            logger.error("user info is null,but system should not null");
            return new UnexpectedException("内部错误，用户信息不存在", HttpStatus.INTERNAL_SERVER_ERROR);
        });
        leave.setId(null);
        leave.setUser(user);
        leave.setStatus(false);
        return leaveDao.save(leave);
    }
}
