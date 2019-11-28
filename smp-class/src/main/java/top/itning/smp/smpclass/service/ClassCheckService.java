package top.itning.smp.smpclass.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpclass.entity.StudentClassCheck;
import top.itning.smp.smpclass.security.LoginUser;

/**
 * @author itning
 */
public interface ClassCheckService {
    /**
     * 获取学生某个群组的签到信息
     *
     * @param studentClassId 学生
     * @param loginUser      登录用户
     * @param pageable       分页
     * @return 学生某个群组的签到信息
     */
    Page<StudentClassCheck> getAllChecks(String studentClassId, LoginUser loginUser, Pageable pageable);
}
