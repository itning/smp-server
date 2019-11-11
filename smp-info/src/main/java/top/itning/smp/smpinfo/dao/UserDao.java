package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.itning.smp.smpinfo.entity.User;

/**
 * @author itning
 */
public interface UserDao extends JpaRepository<User, String>, JpaSpecificationExecutor<User> {
    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    User findByUsername(String username);
}
