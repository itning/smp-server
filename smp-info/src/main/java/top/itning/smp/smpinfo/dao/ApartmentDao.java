package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.Apartment;

/**
 * @author itning
 */
public interface ApartmentDao extends JpaRepository<Apartment, String> {
}
