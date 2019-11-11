package top.itning.smp.smpinfo.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.entity.RestModel;
import top.itning.smp.smpinfo.server.UserService;

import java.io.IOException;

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
                                                    Pageable pageable) {
        return RestModel.ok(userService.getAllUser(pageable));
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
                                                 Pageable pageable) {
        return RestModel.ok(userService.searchUsers(key, pageable));
    }

    /**
     * 更新用户信息
     *
     * @param studentUserDTO 用户信息
     * @return ResponseEntity
     */
    @PatchMapping("/user")
    public ResponseEntity<?> updateUser(@RequestBody StudentUserDTO studentUserDTO) {
        userService.updateUser(studentUserDTO);
        return RestModel.noContent();
    }

    /**
     * 删除用户
     *
     * @param id ID
     * @return ResponseEntity
     */
    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> delUser(@PathVariable String id) {
        userService.delUser(id);
        return RestModel.noContent();
    }

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 上传
     */
    @PostMapping("/user/file")
    public ResponseEntity<?> newUser(@RequestParam("file") MultipartFile file) throws IOException {
        return RestModel.created(userService.upFile(file));
    }
}
