package top.itning.smp.smpclass.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpclass.dto.StudentClassCheckDTO;
import top.itning.smp.smpclass.entity.StudentClassCheck;
import top.itning.smp.smpclass.entity.StudentClassCheckMetaData;
import top.itning.smp.smpclass.security.LoginUser;

import java.util.Date;
import java.util.List;

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


    /**
     * 教师发起签到
     *
     * @param loginUser      登录用户
     * @param longitude      经度
     * @param latitude       纬度
     * @param studentClassId 课堂ID
     * @param m              最远签到距离（米）
     * @param startTime      签到开始时间
     * @param endTime        签到结束时间
     * @return 学生课堂签到元数据
     */
    StudentClassCheckMetaData newCheck(LoginUser loginUser, double longitude, double latitude, String studentClassId, float m, Date startTime, Date endTime);

    /**
     * 获取某个班级某个元数据的签到信息
     *
     * @param studentClassCheckMetaDataId 元数据ID
     * @param loginUser                   登录用户
     * @return 签到信息
     */
    List<StudentClassCheckDTO> getCheckInfoByMetaDataId(String studentClassCheckMetaDataId, LoginUser loginUser);

    /**
     * 获取签到信息
     *
     * @param studentUserName 学生用户名
     * @param studentClassId  班级ID
     * @param loginUser       登录用户
     * @return 签到信息
     */
    List<StudentClassCheckDTO> getUserCheckDetail(String studentUserName, String studentClassId, LoginUser loginUser);
}
