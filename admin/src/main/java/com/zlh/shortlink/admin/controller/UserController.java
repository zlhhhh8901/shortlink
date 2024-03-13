package com.zlh.shortlink.admin.controller;

import com.zlh.shortlink.admin.dto.resp.UserRespDTO;
import com.zlh.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/api/shortlink/v1/user/{username}")
    public UserRespDTO getUserByUserName(@PathVariable("username") String username){
        return userService.getUserByUsername(username);
    }
}
