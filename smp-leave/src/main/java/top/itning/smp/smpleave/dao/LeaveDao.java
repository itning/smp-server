package top.itning.smp.smpleave.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import top.itning.smp.smpleave.entity.Leave;
import top.itning.smp.smpleave.entity.LeaveType;
import top.itning.smp.smpleave.entity.User;

import java.util.Date;
import java.util.List;


/**
 * @author itning
 */
public interface LeaveDao extends JpaRepository<Leave, String>, JpaSpecificationExecutor<Leave> {
    /**
     * 获取请假信息
     *
     * @param status   审核状态
     * @param pageable 分页
     * @return 请假信息
     */
    Page<Leave> findAllByStatus(Boolean status, Pageable pageable);

    /**
     * 根据用户寻找请假信息
     *
     * @param user     用户
     * @param pageable 分页
     * @return 请假信息
     */
    Page<Leave> findAllByUser(User user, Pageable pageable);

    /**
     * 根据学号或姓名查询学生请假信息
     *
     * @param key    学号或姓名
     * @param limit1 分页参数1
     * @param limit2 分页参数2
     * @param status 状态
     * @return 请假信息
     */
    @Query(value = "SELECT student_leave.* FROM student_leave INNER JOIN `user` ON `user`.id = student_leave.user_id INNER JOIN student_user ON student_user.id = `user`.id WHERE student_user.student_id like ?1 or user.name like ?1 and student_leave.status = ?4 order by student_leave.gmt_modified desc limit ?2 , ?3", nativeQuery = true)
    List<Leave> findByKey(String key, int limit1, int limit2, boolean status);

    /**
     * 根据学号或姓名查询学生请假信息数量
     *
     * @param key    学号或姓名
     * @param limit1 分页参数1
     * @param limit2 分页参数2
     * @param status 状态
     * @return 学生请假信息数量
     */
    @Query(value = "SELECT count(*) FROM student_leave INNER JOIN `user` ON `user`.id = student_leave.user_id INNER JOIN student_user ON student_user.id = `user`.id WHERE `user`.`name` like ?1 and student_leave.status=?4 limit ?2,?3", nativeQuery = true)
    long countByKey(String key, int limit1, int limit2, boolean status);

    @Query(value = "SELECT student_leave.* FROM student_leave INNER JOIN `user` ON `user`.id = student_leave.user_id INNER JOIN student_user ON student_user.id = `user`.id WHERE student_user.student_id LIKE ?1 OR `user`.NAME LIKE ?1 AND student_leave.`status` = ?7 AND student_leave.start_time BETWEEN ?2 AND ?3 AND now() < student_leave.end_time AND student_leave.leave_type = ?6 LIMIT ?4,?5;", nativeQuery = true)
    List<Leave> findByAdditionalQuery(String key, Date startTime, Date endTime, int limit1, int limit2, LeaveType leaveType, boolean status);

    @Query(value = "SELECT student_leave.* FROM student_leave INNER JOIN `user` ON `user`.id = student_leave.user_id INNER JOIN student_user ON student_user.id = `user`.id WHERE student_user.student_id LIKE ?1 OR `user`.NAME LIKE ?1 AND student_leave.`status` = ?6 AND student_leave.start_time BETWEEN ?2 AND ?3 AND now() < student_leave.end_time LIMIT ?4,?5;", nativeQuery = true)
    List<Leave> findByAdditionalQuery(String key, Date startTime, Date endTime, int limit1, int limit2, boolean status);

    @Query(value = "SELECT student_leave.* FROM student_leave INNER JOIN `user` ON `user`.id = student_leave.user_id INNER JOIN student_user ON student_user.id = `user`.id WHERE student_user.student_id LIKE ?1 OR `user`.NAME LIKE ?1 AND student_leave.`status` = ?7 AND student_leave.start_time BETWEEN ?2 AND ?3 AND now() > student_leave.end_time AND student_leave.leave_type = ?6 LIMIT ?4,?5;", nativeQuery = true)
    List<Leave> findByAdditionalQueryWithOut(String key, Date startTime, Date endTime, int limit1, int limit2, LeaveType leaveType, boolean status);

    @Query(value = "SELECT student_leave.* FROM student_leave INNER JOIN `user` ON `user`.id = student_leave.user_id INNER JOIN student_user ON student_user.id = `user`.id WHERE student_user.student_id LIKE ?1 OR `user`.NAME LIKE ?1 AND student_leave.`status` = ?6 AND student_leave.start_time BETWEEN ?2 AND ?3 AND now() > student_leave.end_time LIMIT ?4,?5;", nativeQuery = true)
    List<Leave> findByAdditionalQueryWithOut(String key, Date startTime, Date endTime, int limit1, int limit2, boolean status);
}