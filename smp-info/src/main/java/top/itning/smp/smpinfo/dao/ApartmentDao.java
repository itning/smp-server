package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.Apartment;

/**
 * @author itning
 */
public interface ApartmentDao extends JpaRepository<Apartment, String> {
    /**
     * 根据公寓名查询ID
     *
     * @param name 公寓名
     * @return ID
     */
    Apartment findByName(String name);
}
