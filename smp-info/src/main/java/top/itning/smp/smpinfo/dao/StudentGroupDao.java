package top.itning.smp.smpinfo.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpinfo.entity.StudentGroup;
import top.itning.smp.smpinfo.entity.StudentGroupPrimaryKey;

/**
 * @author itning
 */
public interface StudentGroupDao extends JpaRepository<StudentGroup, StudentGroupPrimaryKey> {
}
