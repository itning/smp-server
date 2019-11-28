package top.itning.smp.smproom.client.entity;

import lombok.Data;
import top.itning.smp.smproom.entity.User;

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
