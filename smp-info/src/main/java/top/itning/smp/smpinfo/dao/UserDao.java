package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.User;

/**
 * @author itning
 */
public interface UserDao extends JpaRepository<User, String> {
}
