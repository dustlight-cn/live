package cn.dustlight.live.controllers;

import cn.dustlight.live.Constants;
import cn.dustlight.live.entities.User;
import cn.dustlight.live.entities.Users;
import cn.dustlight.live.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RequestMapping(Constants.API_ROOT)
@RestController
@Tag(name = "Users", description = "用户资源。")
@CrossOrigin
public class UsersController {

    private Log logger = LogFactory.getLog(getClass());

    private UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @SecurityRequirement(name = "oauth")
    @Operation(summary = "获取当前用户信息")
    @GetMapping("user")
    public Mono<User> getCurrentUser(Principal principal) {
        return userService.getUser(Long.valueOf(principal.getName()));
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("users/{uid}")
    public Mono<User> getUser(@PathVariable("uid") Long uid) {
        return userService.getUser(uid);
    }

    @Operation(summary = "获取多个用户信息")
    @GetMapping("users")
    public Mono<Users> getUsers(Long... uid) {
        return userService.getUsers(uid);
    }
}
