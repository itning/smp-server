package top.itning.smp.smproom.client.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import top.itning.smp.smproom.entity.User;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;

/**
 * @author itning
 */
@Data
public class LeaveReason implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 评论用户ID
     */
    private User fromUser;
    /**
     * 评论
     */
    private String comment;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
