package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import top.itning.smp.smpinfo.entity.Apartment;
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

    /**
     * 查看该学号是否重复
     *
     * @param studentId 学号
     * @return 重复
     */
    boolean existsByStudentId(String studentId);

    /**
     * 查看某公寓某寝室床铺是谁
     *
     * @param apartment 公寓
     * @param roomNum   寝室
     * @param bedNum    床号
     * @return 信息
     */
    @Query(value = "select * from student_user s where s.apartment_id=?1 and s.room_num = ?2 and s.bed_num = ?3 limit 1", nativeQuery = true)
    StudentUser findByApartmentAndRoomNumAndBedNum(Apartment apartment, String roomNum, String bedNum);
}
