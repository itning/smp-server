package top.itning.smp.smpclass.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 学生班级学生
 * 管理学生班级与学生之间的关系
 *
 * @author itning
 */
@Data
@Entity(name = "student_class_user")
@IdClass(StudentGroupPrimaryKey.class)
public class StudentClassUser implements Serializable {
    /**
     * 学生 ID
     */
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentId", columnDefinition = "char(36)")
    private User user;
    /**
     * 班级 ID
     */
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentClassId", columnDefinition = "char(36)")
    private StudentClass studentClass;
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
