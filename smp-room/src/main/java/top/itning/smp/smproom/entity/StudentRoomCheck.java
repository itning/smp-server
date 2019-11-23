package top.itning.smp.smproom.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 学生寝室签到表
 *
 * @author itning
 */
@Data
@Entity(name = "student_room_check")
public class StudentRoomCheck implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 学生
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private User user;
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
