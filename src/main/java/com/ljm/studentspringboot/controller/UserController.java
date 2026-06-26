package com.ljm.studentspringboot.controller;

import com.ljm.studentspringboot.dto.UserLoginDTO;
import com.ljm.studentspringboot.dto.UserRegisterDTO;
import com.ljm.studentspringboot.entity.Result;
import com.ljm.studentspringboot.service.UserService;
import com.ljm.studentspringboot.util.UserContext;
import com.ljm.studentspringboot.vo.UserLoginVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public Result<Void> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        userService.register(userRegisterDTO);
        return Result.success();
    }

    @PostMapping("/users/login")
    public Result<UserLoginVO> login(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        return Result.success(userService.login(userLoginDTO));
    }

    @GetMapping("/users/me")
    public Result<UserLoginVO> me() {
        return Result.success(new UserLoginVO(
                UserContext.getUserId(),
                UserContext.getUsername(),
                null
        ));
    }
}
