package com.zlh.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zlh.shortlink.admin.common.convention.result.Result;
import com.zlh.shortlink.admin.common.convention.result.Results;
import com.zlh.shortlink.admin.dto.resp.UserActualRespDTO;
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

    /**
     * 根据用户名查询脱敏用户信息
     */
    @GetMapping("/api/shortlink/v1/user/{username}")
    public Result<UserRespDTO> getUserByUserName(@PathVariable("username") String username){
        return Results.success(userService.getUserByUsername(username));
    }

    @GetMapping("/api/shortlink/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUserName(@PathVariable("username") String username){
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }
}