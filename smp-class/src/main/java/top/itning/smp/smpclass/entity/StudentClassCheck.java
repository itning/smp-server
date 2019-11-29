package top.itning.smp.smpclass.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 学生课堂签到
 * 每个群组的签到信息
 *
 * @author itning
 */
@Data
@Entity(name = "student_class_check")
public class StudentClassCheck implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 学生信息
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private User user;
    /**
     * 所属班级
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentClassId")
    private StudentClass studentClass;
    /**
     * 签到时间
     */
    @Column(nullable = false)
    private Date checkTime;
    /**
     * 经度
     */
    @Column(nullable = false)
    private double longitude;
    /**
     * 纬度
     */
    @Column(nullable = false)
    private double latitude;
    /**
     * 本次签到所对应的元数据
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "studentClassCheckMetaDataId")
    private StudentClassCheckMetaData studentClassCheckMetaData;
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
