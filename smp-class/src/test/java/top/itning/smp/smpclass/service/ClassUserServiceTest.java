package top.itning.smp.smpclass.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import top.itning.smp.smpclass.security.LoginUser;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@SpringBootTest
class ClassUserServiceTest {
    @Autowired
    private ClassUserService classUserService;

    @Test
    void getAllStudentClassUsers() {
        LoginUser loginUser = new LoginUser();
        loginUser.setUsername("");
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "gmtModified"));
        classUserService.getAllStudentClassUsers(loginUser, pageable);
    }

    @Test
    void joinClass() {
    }

    @Test
    void newClass() {
    }

    @Test
    void quitClass() {
    }

    @Test
    void delClass() {
    }

    @Test
    void getAllStudentClass() {
    }

    @Test
    void getAllStudentClassCheckMetaData() {
    }

    @Test
    void getStudentClassLeave() {
    }

    @Test
    void delStudent() {
    }
}