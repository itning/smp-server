package top.itning.smp.smpclass.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 学生班级，由老师创建
 *
 * @author itning
 */
@Data
@Entity(name = "student_class")
public class StudentClass implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 群组名
     */
    @Column(nullable = false)
    private String name;
    /**
     * 班号（创建时教师指定，唯一；学生加入用此）
     */
    @Column(nullable = false, unique = true)
    private String classNum;
    /**
     * 所属教师
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "teacherUserId")
    private User user;
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
