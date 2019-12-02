package top.itning.smp.smpinfo.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dao.ApartmentDao;
import top.itning.smp.smpinfo.dao.StudentUserDao;
import top.itning.smp.smpinfo.dao.UserDao;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.dto.UpFileDTO;
import top.itning.smp.smpinfo.entity.Apartment;
import top.itning.smp.smpinfo.entity.Role;
import top.itning.smp.smpinfo.entity.StudentUser;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.exception.NullFiledException;
import top.itning.utils.uuid.UUIDs;

import java.io.IOException;
import java.util.Date;


@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ApartmentDao apartmentDao;
    @Autowired
    private StudentUserDao studentUserDao;

    @Test
    void getAllUser() {
        Assertions.assertNotNull(userService.getAllUser());
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "gmtModified"));
        Assertions.assertNotNull(userService.getAllUser(pageable));
    }

    @Test
    void searchUsers() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "gmtModified"));
        Assertions.assertNotNull(userService.searchUsers("", pageable));
        Assertions.assertNotNull(userService.searchUsers(null, pageable));
        Assertions.assertNotNull(userService.searchUsers("1", pageable));
    }

    @Test
    void updateUser() {
        Assertions.assertThrows(NullFiledException.class, () -> userService.updateUser(null));
        Assertions.assertThrows(NullFiledException.class, () -> userService.updateUser(new StudentUserDTO()));
        Assertions.assertThrows(NullFiledException.class, () -> {
            StudentUserDTO studentUserDTO = new StudentUserDTO();
            studentUserDTO.setId(UUIDs.get());
            userService.updateUser(studentUserDTO);
        });

        User user = new User();
        user.setName("测试用名字");
        user.setTel("15636359874");
        user.setEmail("itning@itning.top");
        user.setUsername("testusername");
        user.setPassword("testpassword");
        user.setRole(Role.withStudentUser());
        User saved = userDao.save(user);

        Apartment apartment = new Apartment();
        apartment.setName("测试用");
        Apartment savedApartment = apartmentDao.save(apartment);

        StudentUser studentUser = new StudentUser();
        studentUser.setId(saved.getId());
        studentUser.setBirthday(new Date());
        studentUser.setSex(false);
        studentUser.setAge(12);
        studentUser.setStudentId("20161441");
        studentUser.setIdCard("232301199708085433");
        studentUser.setPoliticalStatus("中共党员");
        studentUser.setEthnic("高山");
        studentUser.setApartment(savedApartment);
        studentUser.setRoomNum("1");
        studentUser.setAddress("1");
        studentUser.setBedNum("1");
        StudentUser savedStudentUser = studentUserDao.save(studentUser);
        saved.setStudentUser(savedStudentUser);
        userDao.save(saved);

        StudentUserDTO studentUserDTO = new StudentUserDTO();
        studentUserDTO.setId(saved.getId());
        studentUserDTO.setName(saved.getName() + "0");
        studentUserDTO.setTel(saved.getTel() + "0");
        studentUserDTO.setEmail(saved.getEmail() + "0");
        studentUserDTO.setUsername(saved.getUsername() + "0");
        studentUserDTO.setRole(Role.withStudentUser());
        studentUserDTO.setStudentId("22222");
        studentUserDTO.setIdCard("232301199708085433");

        userService.updateUser(studentUserDTO);

        User savedd = userDao.findById(saved.getId()).orElseThrow(() -> new IllegalStateException("存储User失败"));

        Assertions.assertEquals(saved.getName() + "0", savedd.getName());
        Assertions.assertEquals(saved.getTel() + "0", savedd.getTel());
        Assertions.assertEquals(saved.getEmail() + "0", savedd.getEmail());
        Assertions.assertEquals(saved.getUsername() + "0", savedd.getUsername());
        Assertions.assertEquals("22222", savedd.getStudentUser().getStudentId());
        Assertions.assertEquals("232301199708085433", savedd.getStudentUser().getIdCard());

        userDao.delete(saved);
        studentUserDao.delete(savedStudentUser);
        apartmentDao.delete(savedApartment);
    }

    @Test
    void delUser() {
        Assertions.assertThrows(NullFiledException.class, () -> userService.delUser(null));
        Assertions.assertThrows(NullFiledException.class, () -> userService.delUser(""));

        User user = new User();
        user.setName("测试用名字");
        user.setTel("15636359874");
        user.setEmail("itning@itning.top");
        user.setUsername("testusername");
        user.setPassword("testpassword");
        user.setRole(Role.withStudentUser());
        User saved = userDao.save(user);

        Apartment apartment = new Apartment();
        apartment.setName("测试用");
        Apartment savedApartment = apartmentDao.save(apartment);

        StudentUser studentUser = new StudentUser();
        studentUser.setId(saved.getId());
        studentUser.setBirthday(new Date());
        studentUser.setSex(false);
        studentUser.setAge(12);
        studentUser.setStudentId("20161441");
        studentUser.setIdCard("232301199708085433");
        studentUser.setPoliticalStatus("中共党员");
        studentUser.setEthnic("高山");
        studentUser.setApartment(savedApartment);
        studentUser.setRoomNum("1");
        studentUser.setAddress("1");
        studentUser.setBedNum("1");
        StudentUser savedStudentUser = studentUserDao.save(studentUser);
        saved.setStudentUser(savedStudentUser);
        userDao.save(saved);

        userService.delUser(saved.getId());
        Assertions.assertNull(userDao.findByUsername("测试用名字"));

        studentUserDao.delete(savedStudentUser);
        apartmentDao.delete(savedApartment);
    }

    @Test
    void upFile() throws IOException {
        Apartment apartment = new Apartment();
        apartment.setName("测试用");
        Apartment savedApartment = apartmentDao.save(apartment);

        byte[] bytes = FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:test.xlsx"));
        MultipartFile multipartFile = new MockMultipartFile("测试文件1.xlsx", "测试文件1.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, bytes);
        userService.upFile(multipartFile);

        User user = userDao.findByUsername("2016022135");

        Assertions.assertNotNull(user);

        StudentUser studentUser = user.getStudentUser();

        Assertions.assertNotNull(studentUser);

        byte[] bytes2 = FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:test2.xlsx"));
        MultipartFile multipartFile2 = new MockMultipartFile("测试文件2.xlsx", null, MediaType.APPLICATION_OCTET_STREAM_VALUE, bytes2);
        UpFileDTO upFileDTO2 = userService.upFile(multipartFile2);
        Assertions.assertFalse(upFileDTO2.getError().isEmpty());

        userDao.delete(user);
        studentUserDao.delete(studentUser);
        apartmentDao.delete(savedApartment);
    }

    @Test
    void getUserInfoByUserName() {
        Assertions.assertNull(userService.getUserInfoByUserName(""));
        Assertions.assertNull(userService.getUserInfoByUserName(null));

        User user = new User();
        user.setName("测试用名字");
        user.setTel("15636359874");
        user.setEmail("itning@itning.top");
        user.setUsername("testusername");
        user.setPassword("testpassword");
        user.setRole(Role.withStudentUser());
        User saved = userDao.save(user);

        Assertions.assertNotNull(userService.getUserInfoByUserName(saved.getUsername()));

        userDao.delete(saved);
    }

    @Test
    void getStudentUserInfoByUserName() {
        Assertions.assertNull(userService.getStudentUserInfoByUserName(""));
        Assertions.assertNull(userService.getStudentUserInfoByUserName(null));

        User user = new User();
        user.setName("测试用名字");
        user.setTel("15636359874");
        user.setEmail("itning@itning.top");
        user.setUsername("testusername");
        user.setPassword("testpassword");
        user.setRole(Role.withStudentUser());
        User saved = userDao.save(user);

        Apartment apartment = new Apartment();
        apartment.setName("测试用");
        Apartment savedApartment = apartmentDao.save(apartment);

        StudentUser studentUser = new StudentUser();
        studentUser.setId(saved.getId());
        studentUser.setBirthday(new Date());
        studentUser.setSex(false);
        studentUser.setAge(12);
        studentUser.setStudentId("20161441");
        studentUser.setIdCard("232301199708085433");
        studentUser.setPoliticalStatus("中共党员");
        studentUser.setEthnic("高山");
        studentUser.setApartment(savedApartment);
        studentUser.setRoomNum("1");
        studentUser.setAddress("1");
        studentUser.setBedNum("1");
        StudentUser savedStudentUser = studentUserDao.save(studentUser);
        saved.setStudentUser(savedStudentUser);
        userDao.save(saved);

        Assertions.assertNotNull(userService.getStudentUserInfoByUserName(saved.getUsername()));

        studentUserDao.delete(savedStudentUser);

        Assertions.assertNull(userService.getStudentUserInfoByUserName(saved.getUsername()));

        userDao.delete(saved);
        apartmentDao.delete(savedApartment);
    }

    @Test
    void countStudent() {
        Assertions.assertDoesNotThrow(() -> userService.countStudent());
    }

    @Test
    void testGetAllUser() {
        Assertions.assertNotNull(userService.getAllUser());
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "gmtModified"));
        Assertions.assertNotNull(userService.getAllUser(pageable));
    }
}