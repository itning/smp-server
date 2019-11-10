package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import top.itning.smp.smpinfo.entity.StudentUser;

/**
 * @author itning
 */
public interface StudentUserDao extends JpaRepository<StudentUser, String>, JpaSpecificationExecutor<StudentUser> {
    /**
     * 计算公寓人数
     *
     * @param apartmentId 公寓ID
     * @return 人数
     */
    @Query(name = "select count(*) from student_user where apartment_id = ?", nativeQuery = true)
    long countByApartmentId(String apartmentId);
}
