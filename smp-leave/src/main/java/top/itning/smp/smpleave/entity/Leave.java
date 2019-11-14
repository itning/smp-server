package top.itning.smp.smpleave.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 请假表
 *
 * @author itning
 */
@Data
@Entity(name = "student_leave")
public class Leave implements Serializable {
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
     * 请假开始时间
     */
    @Column(nullable = false)
    private Date startTime;
    /**
     * 请假结束时间
     */
    @Column(nullable = false)
    private Date endTime;
    /**
     * 请假类型
     */
    @Column(nullable = false)
    private LeaveType leaveType;
    /**
     * 请假原因
     */
    @Column(nullable = false, columnDefinition = "text")
    private String reason;
    /**
     * 审核状态（true 通过；false 未通过；null 未审核）
     */
    @Column
    private Boolean status;
    /**
     * 评论
     */
    @OneToMany
    @JoinColumn(name = "leaveId")
    private List<LeaveReason> leaveReasonList;
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
