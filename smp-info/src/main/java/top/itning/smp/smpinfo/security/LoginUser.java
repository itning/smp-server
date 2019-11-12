package top.itning.smp.smpinfo.security;

import lombok.Data;

import java.io.Serializable;

/**
 * @author itning
 */
@Data
public class LoginUser implements Serializable {
    private String name;
    private String username;
    private String roleId;
    private String email;
    private String tel;
}
