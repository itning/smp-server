package top.itning.smp.smproom.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smproom.entity.StudentRoomCheck;
import top.itning.smp.smproom.entity.User;

/**
 * @author itning
 */
public interface StudentRoomCheckDao extends JpaRepository<StudentRoomCheck, String> {
    /**
     * 根据用户查询寝室打卡信息
     *
     * @param user     用户
     * @param pageable 分页
     * @return 用户寝室打卡信息
     */
    Page<StudentRoomCheck> findAllByUser(User user, Pageable pageable);
}
