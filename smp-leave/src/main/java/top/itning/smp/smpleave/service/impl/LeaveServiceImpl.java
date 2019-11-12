package top.itning.smp.smpleave.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smpleave.client.InfoClient;
import top.itning.smp.smpleave.client.entity.StudentUser;
import top.itning.smp.smpleave.dao.LeaveDao;
import top.itning.smp.smpleave.dto.LeaveDTO;
import top.itning.smp.smpleave.dto.SearchDTO;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.User;
import top.itning.smp.smpleave.exception.UnexpectedException;
import top.itning.smp.smpleave.security.LoginUser;
import top.itning.smp.smpleave.service.LeaveService;
import top.itning.smp.smpleave.util.OrikaUtils;

import java.util.List;
import java.util.stream.Collectors;

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
    public Page<LeaveDTO> getLeaves(Pageable pageable) {
        return leaveDao.findAllByStatus(true, pageable).map(leave -> {
            StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElse(null);
            LeaveDTO leaveDTO = OrikaUtils.a2b(leave, LeaveDTO.class);
            leaveDTO.setStudentUser(studentUser);
            return leaveDTO;
        });
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

    @Override
    public Page<LeaveDTO> search(SearchDTO searchDTO, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        String key = searchDTO.getKey();
        List<LeaveDTO> leaves = leaveDao.findByKey("%" + key + "%", pageNumber * pageSize, pageSize, true)
                .parallelStream()
                .map(leave -> {
                    StudentUser studentUser = infoClient.getStudentUserInfoByUserName(leave.getUser().getUsername()).orElse(null);
                    LeaveDTO leaveDTO = OrikaUtils.a2b(leave, LeaveDTO.class);
                    leaveDTO.setStudentUser(studentUser);
                    return leaveDTO;
                }).collect(Collectors.toList());
        return new PageImpl<>(leaves, pageable, leaveDao.countByKey("%" + key + "%", pageNumber * pageSize, pageSize, true));
    }
}
