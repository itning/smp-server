package top.itning.smp.smproom.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
@Data
@IdClass(StudentRoomCheckMetaDataPrimaryKey.class)
@Entity(name = "student_room_check_meta_data")
public class StudentRoomCheckMetaData implements Serializable {
    /**
     * 学生寝室打卡时间
     */
    public static final String KEY_ROOM_CHECK_TIME = "room_check_time";
    /**
     * 学生寝室打卡坐标范围
     */
    public static final String KEY_ROOM_CHECK_GPS_RANGE = "room_check_gps_range";
    /**
     * KEY
     */
    @Id
    @Column(name = "id", length = 50, columnDefinition = "char(50)")
    private String key;
    /**
     * VALUE
     */
    @Column(nullable = false, columnDefinition = "text")
    private String value;
    /**
     * 所属辅导员的ID
     */
    @Id
    @Column(nullable = false, columnDefinition = "char(36)", length = 36)
    private String belongCounselorId;
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
