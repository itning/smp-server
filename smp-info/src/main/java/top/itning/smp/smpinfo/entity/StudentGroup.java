package top.itning.smp.smpinfo.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 学生群组
 *
 * @author itning
 */
@Data
@Entity(name = "student_group")
@IdClass(StudentGroupPrimaryKey.class)
public class StudentGroup implements Serializable {
    /**
     * StudentGroup ID
     */
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentId")
    private User user;
    /**
     * StudentClass ID
     */
    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentClassId")
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
