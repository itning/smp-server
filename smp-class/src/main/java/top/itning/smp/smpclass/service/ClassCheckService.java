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

    /**
     * 可以签到？
     *
     * @param studentClassId 班级ID
     * @param loginUser      登录用户
     * @return 可以签到返回<code>true</code>
     */
    boolean canCheck(String studentClassId, LoginUser loginUser);

    /**
     * 学生课堂打卡
     *
     * @param loginUser      登录用户
     * @param studentClassId 学生班级ID
     * @param longitude      经度
     * @param latitude       纬度
     * @return 学生课堂签到
     */
    StudentClassCheck check(LoginUser loginUser, String studentClassId, double longitude, double latitude);
}
