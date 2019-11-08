package top.itning.smp.smpinfo.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.itning.smp.smpinfo.entity.Role;
import top.itning.smp.smpinfo.entity.User;
import top.itning.utils.uuid.UUIDs;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author itning
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
public class UserDaoTest {
    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    private Role randomRole;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName(UUIDs.get());
        Date date = new Date();
        role.setGmtCreate(date);
        role.setGmtModified(date);
        randomRole = roleDao.save(role);
    }

    @AfterEach
    void tearDown() {
        roleDao.delete(randomRole);
        assertFalse(roleDao.findById(randomRole.getId()).isPresent());
    }

    @Test
    void testUserDao() {
        System.out.println(randomRole);
        String randomUsername = UUIDs.get();
        User user = new User();
        user.setName("itning");
        user.setTel("17568894423");
        user.setEmail("itning@itning.top");
        user.setUsername(randomUsername);
        user.setPassword("ningning");
        user.setRole(randomRole);
        user.setGmtCreate(new Date());
        user.setGmtModified(new Date());
        User savedUser = userDao.save(user);

        assertTrue(userDao.findById(savedUser.getId()).isPresent());

        userDao.delete(user);

        assertFalse(userDao.findById(savedUser.getId()).isPresent());
    }
}
