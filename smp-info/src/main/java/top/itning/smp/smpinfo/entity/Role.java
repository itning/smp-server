package top.itning.smp.smpinfo.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色
 *
 * @author itning
 */
@Data
@Entity(name = "role")
public class Role implements Serializable {
    public static Role withStudentUser() {
        Role role = new Role();
        role.setId("1");
        return role;
    }

    /**
     * 角色ID
     */
    @Id
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 角色名
     */
    @Column(length = 50, unique = true, nullable = false)
    private String name;
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
