package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.Role;

/**
 * @author itning
 */
public interface RoleDao extends JpaRepository<Role, String> {
}
