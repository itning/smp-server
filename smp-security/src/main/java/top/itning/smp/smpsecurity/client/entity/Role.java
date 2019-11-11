package top.itning.smp.smpsecurity.client.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 *
 * @author itning
 */
@Data
public class Role implements Serializable {
    /**
     * 角色ID
     */
    private String id;
    /**
     * 角色名
     */
    private String name;
    /**
     * 创建时间
     */
    private Date gmtCreate;
    /**
     * 更新时间
     */
    private Date gmtModified;
}
