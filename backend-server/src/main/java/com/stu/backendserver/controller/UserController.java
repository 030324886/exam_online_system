package com.stu.backendserver.controller;

import com.stu.backendserver.common.Result;
import com.stu.backendserver.entity.User;
import com.stu.backendserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user) {
        try {
            User loginUser = userService.login(user);
            if (loginUser != null) {
                return Result.success(loginUser);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("服务器异常：" + e.getMessage());
        }
    }
}