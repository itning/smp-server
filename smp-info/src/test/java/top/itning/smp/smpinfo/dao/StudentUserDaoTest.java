package top.itning.smp.smpinfo.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.utils.uuid.UUIDs;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author itning
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
public class StudentUserDaoTest {
    @Autowired
    private StudentUserDao studentUserDao;

    @Autowired
    private ApartmentDao apartmentDao;

    private Apartment randomApartment;

    @BeforeEach
    void setUp() {
        Apartment apartment = new Apartment();
        apartment.setName(UUIDs.get());
        apartment.setGmtCreate(new Date());
        apartment.setGmtModified(new Date());
        randomApartment = apartmentDao.save(apartment);
    }

    @AfterEach
    void tearDown() {
        apartmentDao.delete(randomApartment);
        assertFalse(apartmentDao.findById(randomApartment.getId()).isPresent());
    }

    @Test
    void testStudentUserDao() {
        StudentUser studentUser = new StudentUser();
        studentUser.setId(UUIDs.get());
        studentUser.setBirthday(new Date());
        studentUser.setSex(true);
        studentUser.setAge(21);
        studentUser.setStudentId("201601010317");
        studentUser.setIdCard("232301199805085418");
        studentUser.setPoliticalStatus("共青团员");
        studentUser.setEthnic("汉");
        studentUser.setApartment(randomApartment);
        studentUser.setBedNum("1");
        studentUser.setGmtCreate(new Date());
        studentUser.setGmtModified(new Date());
        StudentUser savedStudentUser = studentUserDao.save(studentUser);

        assertTrue(studentUserDao.findById(savedStudentUser.getId()).isPresent());

        studentUserDao.delete(studentUser);

        assertFalse(studentUserDao.findById(savedStudentUser.getId()).isPresent());
    }
}
