package top.itning.smp.smpclass.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.security.LoginUser;

/**
 * @author itning
 */
public interface ClassUserService {
    /**
     * 根据登录用户查询
     *
     * @param loginUser 登录用户
     * @param pageable  分页
     * @return 学生班级
     */
    Page<StudentClassUser> getAllStudentClassUsers(LoginUser loginUser, Pageable pageable);
}