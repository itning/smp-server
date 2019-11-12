package top.itning.smp.smpinfo.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.itning.smp.smpinfo.entity.Role;
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

    /**
     * 根据角色查找
     *
     * @param role     角色
     * @param pageable 分页
     * @return 用户
     */
    Page<User> findByRole(Role role, Pageable pageable);
}
