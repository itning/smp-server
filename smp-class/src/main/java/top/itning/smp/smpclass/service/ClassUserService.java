package top.itning.smp.smpclass.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import top.itning.smp.smpclass.client.entity.LeaveDTO;
import top.itning.smp.smpclass.dto.StudentClassDTO;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.StudentClassCheckMetaData;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.security.LoginUser;

import java.util.Date;
import java.util.List;

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

    /**
     * 学生加入班级
     *
     * @param loginUser 登录用户
     * @param classNum  班号
     * @return 加入的班级
     */
    StudentClassUser joinClass(LoginUser loginUser, String classNum);

    /**
     * 创建班级
     *
     * @param loginUser 登录用户
     * @param name      班级名称
     * @return 创建的班级
     */
    StudentClass newClass(LoginUser loginUser, String name);

    /**
     * 学生退出班级
     *
     * @param loginUser      登录用户
     * @param studentClassId 班级ID
     */
    void quitClass(LoginUser loginUser, String studentClassId);

    /**
     * 教师解散班级
     *
     * @param loginUser      登录用户
     * @param studentClassId 班级ID
     */
    void delClass(LoginUser loginUser, String studentClassId);

    /**
     * 获取教师创建的班级
     *
     * @param loginUser 登录用户
     * @param pageable  分页
     * @return 教师创建的班级
     */
    Page<StudentClassDTO> getAllStudentClass(LoginUser loginUser, Pageable pageable);

    /**
     * 获取所有签到元数据
     *
     * @param studentClassId 班级ID
     * @param loginUser      登录用户
     * @param pageable       分页
     * @return 所有签到元数据
     */
    Page<StudentClassCheckMetaData> getAllStudentClassCheckMetaData(String studentClassId, LoginUser loginUser, Pageable pageable);

    /**
     * 获取某班级的请假信息
     *
     * @param loginUser      登录用户
     * @param studentClassId 班级ID
     * @param whereDay       哪天
     * @return 某班级的请假信息集合
     */
    List<LeaveDTO> getStudentClassLeave(LoginUser loginUser, String studentClassId, Date whereDay);

    /**
     * 教师删除自己班级的学生
     *
     * @param studentUserName 学生用户名
     * @param studentClassId  学生班级ID
     * @param loginUser       登录用户
     */
    void delStudent(String studentUserName, String studentClassId, LoginUser loginUser);
}
