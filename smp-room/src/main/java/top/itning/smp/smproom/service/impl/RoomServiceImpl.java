package top.itning.smp.smproom.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smproom.client.InfoClient;
import top.itning.smp.smproom.dao.StudentRoomCheckDao;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.entity.User;
import top.itning.smp.smproom.exception.SavedException;
import top.itning.smp.smproom.exception.UserNameDoesNotExistException;
import top.itning.smp.smproom.security.LoginUser;
import top.itning.smp.smproom.service.RoomService;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @author itning
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoomServiceImpl implements RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
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

    @Override
    public StudentRoomCheck check(MultipartFile file, LoginUser loginUser) throws IOException {
        User user = infoClient.getUserInfoByUserName(loginUser.getUsername()).orElseThrow(() -> new UserNameDoesNotExistException("用户名不存在", HttpStatus.NOT_FOUND));
        StudentRoomCheck studentRoomCheck = new StudentRoomCheck();
        studentRoomCheck.setUser(user);
        studentRoomCheck.setCheckTime(new Date());
        StudentRoomCheck saved = studentRoomCheckDao.save(studentRoomCheck);
        String filenameExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (filenameExtension == null) {
            logger.warn("use default extension for path {}", file.getOriginalFilename());
            filenameExtension = "jpg";
        }
        if (!StringUtils.hasText(saved.getId())) {
            throw new SavedException("数据库存储ID为空", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        file.transferTo(new File("C:\\Users\\wangn\\Desktop\\" + saved.getId() + "." + filenameExtension));
        return saved;
    }
}
