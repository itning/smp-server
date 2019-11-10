package top.itning.smp.smpinfo.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.itning.smp.smpinfo.dto.StudentUserDTO;
import top.itning.smp.smpinfo.entity.RestModel;
import top.itning.smp.smpinfo.server.UserService;

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
    public RestModel<?> getAllUserInfo(@PageableDefault(size = 20, sort = {"gmtModified"}, direction = Sort.Direction.DESC)
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
    public RestModel<?> searchUsers(@PathVariable String key,
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
    public ResponseEntity<Void> updateUser(@RequestBody StudentUserDTO studentUserDTO) {
        userService.updateUser(studentUserDTO);
        return ResponseEntity.noContent().build();
    }
}
