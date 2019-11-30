package top.itning.smp.smpclass.dto;

import lombok.Data;
import top.itning.smp.smpclass.entity.StudentClassUser;
import top.itning.smp.smpclass.entity.User;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
@Data
public class StudentClassDTO implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 群组名
     */
    private String name;
    /**
     * 班号（创建时教师指定，唯一；学生加入用此）
     */
    private String classNum;
    /**
     * 加入该班级的学生
     */
    private List<StudentClassUser> studentClassUserList;
    /**
     * 所属教师
     */
    private User user;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
