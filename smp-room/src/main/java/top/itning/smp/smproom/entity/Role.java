package top.itning.smp.smproom.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 *
 * @author itning
 */
@Data
@Entity(name = "role")
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
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 角色名
     */
    @Column(length = 50, unique = true, nullable = false)
    private String name;
    /**
     * 创建时间
     */
    @Column(nullable = false)
    @CreationTimestamp
    private Date gmtCreate;
    /**
     * 更新时间
     */
    @Column(nullable = false)
    @UpdateTimestamp
    private Date gmtModified;
}
