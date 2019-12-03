package top.itning.smp.smpinfo.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.entity.RestModel;
import top.itning.smp.smpinfo.entity.User;
import top.itning.smp.smpinfo.security.LoginUser;
import top.itning.smp.smpinfo.security.MustCounselorLogin;
import top.itning.smp.smpinfo.service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * 用户控制层
 *
 * @author itning
 */
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 分页获取学生信息
     *
     * @param pageable 分页信息
     * @return RestModel
     */
    @GetMapping("/users")

    public ResponseEntity<?> getAllUserInfo(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
                                                    Pageable pageable,
                                            @MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(userService.getAllUser(pageable, loginUser));
    }

    /**
     * 搜索用户
     *
     * @param key      用户名
     * @param pageable 分页信息
     * @return RestModel
     */
    @GetMapping("/search/users/{key}")
    public ResponseEntity<?> searchUsers(@PathVariable String key,
                                         @PageableDefault(size = 20, sort = {"gmtModified"},
                                                 direction = Sort.Direction.DESC)
                                                 Pageable pageable,
                                         @MustCounselorLogin LoginUser loginUser) {
        return RestModel.ok(userService.searchUsers(key, pageable, loginUser));
    }

    /**
     * 更新用户信息
     *
     * @param studentUserDTO 用户信息
     * @return ResponseEntity
     */
    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody StudentUserDTO studentUserDTO,
                                        @MustCounselorLogin LoginUser loginUser) {
        userService.updateUser(studentUserDTO, loginUser);
        return RestModel.noContent();
    }

    /**
     * 删除用户
     *
     * @param id ID
     * @return ResponseEntity
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> delUser(@PathVariable String id,
                                     @MustCounselorLogin LoginUser loginUser) {
        userService.delUser(id, loginUser);
        return RestModel.noContent();
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传
     */
    @PostMapping("/user/file")
    public ResponseEntity<?> newUser(@RequestParam("file") MultipartFile file,
                                     @MustCounselorLogin LoginUser loginUser) throws IOException {
        return RestModel.created(userService.upFile(file, loginUser));
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/internal/user/{username}")
    public User getUserInfoByUserName(@PathVariable String username) {
        return userService.getUserInfoByUserName(username);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/internal/student_user/{username}")
    public StudentUserDTO getStudentUserDtoFromUserName(@PathVariable String username) {
        return userService.getStudentUserInfoByUserName(username);
    }

    /**
     * 计算学生人数
     *
     * @param username 导员用户名
     * @return 学生数量
     */
    @GetMapping("/internal/student_user/count/{username}")
    public long countStudent(@PathVariable String username) {
        return userService.countStudent(username);
    }

    /**
     * 获取所有学生信息
     *
     * @param username 导员用户名
     * @return 学生信息
     */
    @GetMapping("/internal/users")
    public List<StudentUserDTO> getAllUser(@RequestParam String username) {
        return userService.getAllUser(username);
    }
}
