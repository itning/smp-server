package top.itning.smp.smpleave.entity;

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
@Entity(name = "leave_reason")
public class LeaveReason implements Serializable {
    /**
     * ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 评论用户ID
     */
    @OneToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;
    /**
     * 评论
     */
    @Column(nullable = false, columnDefinition = "text")
    private String comment;
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
