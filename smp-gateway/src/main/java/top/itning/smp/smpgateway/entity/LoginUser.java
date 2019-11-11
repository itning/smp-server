package top.itning.smp.smpgateway.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class LoginUser implements Serializable {
    private String name;
    private String username;
    private Role role;
    private String email;
    private String tel;
}
