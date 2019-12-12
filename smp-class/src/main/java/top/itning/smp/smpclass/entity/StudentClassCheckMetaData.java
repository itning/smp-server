package top.itning.smp.smpclass.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
@Data
@Entity(name = "student_class_check_meta_data")
public class StudentClassCheckMetaData implements Serializable {
    /**
     * 元数据ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 签到开始时间
     */
    @Column(nullable = false)
    private Date startTime;
    /**
     * 签到结束时间
     */
    @Column(nullable = false)
    private Date endTime;
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
     * 签到者距离教师经纬度最大距离
     */
    @Column(nullable = false)
    private float m;
    /**
     * 元数据所对应的班级
     */
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
