package top.itning.smp.smpclass.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import top.itning.smp.smpclass.entity.StudentGroup;
import top.itning.smp.smpclass.entity.StudentGroupPrimaryKey;


/**
 * @author itning
 */
public interface StudentGroupDao extends JpaRepository<StudentGroup, StudentGroupPrimaryKey> {
}
