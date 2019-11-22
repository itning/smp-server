package top.itning.smp.smproom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.itning.smp.smproom.client.InfoClient;
import top.itning.smp.smproom.dao.StudentRoomCheckDao;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.entity.User;
import top.itning.smp.smproom.exception.UserNameDoesNotExistException;
import top.itning.smp.smproom.service.RoomService;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoomServiceImpl implements RoomService {
    private final StudentRoomCheckDao studentRoomCheckDao;
    private final InfoClient infoClient;

    @Autowired
    public RoomServiceImpl(StudentRoomCheckDao studentRoomCheckDao, InfoClient infoClient) {
        this.studentRoomCheckDao = studentRoomCheckDao;
        this.infoClient = infoClient;
    }

    @Override
    public Page<StudentRoomCheck> getRoomCheckInfoByStudentUserName(String username, Pageable pageable) {
        User user = infoClient.getUserInfoByUserName(username).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        return studentRoomCheckDao.findAllByUser(user, pageable);
    }
}
