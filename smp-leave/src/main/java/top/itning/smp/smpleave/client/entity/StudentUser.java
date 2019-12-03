package top.itning.smp.smpleave.client.entity;

import lombok.Data;

import top.itning.smp.smpleave.entity.Role;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生信息DTO
 *
 * @author itning
 */
@Data
public class StudentUser implements Serializable {
    /**
     * 用户ID
     */
    private String id;
    /**
     * 用户姓名
     */
    private String name;
    /**
     * 电话
     */
    private String tel;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    private String username;
    /**
     * 角色
     */
    private Role role;
    /**
     * 出生日期
     */
    private Date birthday;
    /**
     * 性别（true 男； false 女）
     */
    private Boolean sex;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 学号
     */
    private String studentId;
    /**
     * 该学生所属辅导员的ID
     */
    private String belongCounselorId;
    /**
     * 身份证号
     */
    private String idCard;
    /**
     * 政治面貌
     */
    private String politicalStatus;
    /**
     * 民族
     */
    private String ethnic;
    /**
     * 公寓信息
     */
    private Apartment apartment;
    /**
     * 寝室号
     */
    private String roomNum;
    /**
     * 床号
     */
    private String bedNum;
    /**
     * 家庭地址
     */
    private String address;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
