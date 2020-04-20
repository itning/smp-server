package top.itning.smp.smpleave.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author itning
 */
@Data
@Entity(name = "user")
public class User implements Serializable {
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36, columnDefinition = "char(36)")
    private String id;
    /**
     * 用户姓名
     */
    @Column(nullable = false)
    private String name;
    /**
     * 电话
     */
    @Column(length = 11, columnDefinition = "char(11)")
    private String tel;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 用户名
     */
    @Column(nullable = false, unique = true)
    private String username;
    /**
     * 角色
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "roleId")
    private Role role;
    /**
     * 所对应的学生
     */
    @OneToOne
    @PrimaryKeyJoinColumn
    private StudentUser studentUser;
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
