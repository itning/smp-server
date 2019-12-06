package top.itning.smp.smpstatistics.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 *
 * @author itning
 */
@Data
public class Role implements Serializable {
    public static final String STUDENT_ROLE_ID = "1";
    public static final String STUDENT_ROLE_ID_STR = "学生";
    public static final String TEACHER_ROLE_ID = "2";
    public static final String TEACHER_ROLE_ID_STR = "教师";
    public static final String COUNSELOR_ROLE_ID = "3";
    public static final String COUNSELOR_ROLE_ID_STR = "辅导员";

    /**
     * 角色ID
     */
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
