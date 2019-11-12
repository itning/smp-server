package top.itning.smp.smpleave.client.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 公寓信息
 *
 * @author itning
 */
@Data
public class Apartment implements Serializable {
    /**
     * ID
     */
    private String id;
    /**
     * 公寓名
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
