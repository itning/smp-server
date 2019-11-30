package top.itning.smp.smpclass.dto;

import lombok.Data;
import top.itning.smp.smpclass.entity.StudentClass;
import top.itning.smp.smpclass.entity.User;

import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
@Data
public class StudentClassCheckDTO implements Serializable {
    /**
     * 学生 ID
     */
    private User user;
    /**
     * 班级 ID
     */
    private StudentClass studentClass;
    /**
     * 该学生是否签到了
     */
    private Boolean check;
    /**
     * 签到时间
     */
    private Date checkTime;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
