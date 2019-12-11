package top.itning.smp.smproom.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.entity.User;

import java.util.Date;
import java.util.List;

/**
 * @author itning
 */
public interface StudentRoomCheckDao extends JpaRepository<StudentRoomCheck, String>, JpaSpecificationExecutor<StudentRoomCheck> {
    /**
     * 根据用户查询寝室打卡信息
     *
     * @param user     用户
     * @param pageable 分页
     * @return 用户寝室打卡信息
     */
    Page<StudentRoomCheck> findAllByUser(User user, Pageable pageable);

    /**
     * 获取范围打卡时间的学生打卡信息
     *
     * @param start 开始
     * @param end   结束
     * @return 学生打卡信息
     */
    List<StudentRoomCheck> findAllByCheckTimeBetweenOrderByCheckTimeDesc(Date start, Date end);

    /**
     * 检查学生某天是否打卡
     *
     * @param user  学生
     * @param start 开始日期
     * @param end   结束日期
     * @return 是否打卡
     */
    boolean existsByUserAndCheckTimeBetween(User user, Date start, Date end);

    /**
     * 删除请假信息
     *
     * @param user 哪个学生
     */
    void deleteAllByUser(User user);

    /**
     * 根据用户查询寝室打卡信息
     *
     * @param user 用户
     * @return 用户寝室打卡信息
     */
    List<StudentRoomCheck> findAllByUser(User user);
}
