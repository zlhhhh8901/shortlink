package com.zlh.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zlh.shortlink.admin.common.convention.result.Result;
import com.zlh.shortlink.admin.common.convention.result.Results;
import com.zlh.shortlink.admin.dto.req.UserLoginReqDto;
import com.zlh.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.zlh.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.zlh.shortlink.admin.dto.resp.UserActualRespDTO;
import com.zlh.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.zlh.shortlink.admin.dto.resp.UserRespDTO;
import com.zlh.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查询脱敏用户信息
     */
    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserRespDTO> getUserByUserName(@PathVariable("username") String username){
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 根据用户名查询无脱敏用户信息
     */
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserActualRespDTO> getActualUserByUserName(@PathVariable("username") String username){
        return Results.success(BeanUtil.toBean(userService.getUserByUsername(username), UserActualRespDTO.class));
    }

    /**
     * 查询用户名是否可用，即查询用户名是否存在
     * 存在则不可用，返回false
     */
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUserName(@RequestParam("username") String username){
        return Results.success(userService.hasUserName(username));
    }

    /**
     * 注册用户
     */
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO registerReqDTO){
        userService.register(registerReqDTO);
        return Results.success();
    }

    /**
     * 修改用户
     */
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO updateReqDTO){
        userService.update(updateReqDTO);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDto requestDto){
        return Results.success(userService.login(requestDto));
    }

    /**
     * 检查用户是否登录
     */
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username")String username, @RequestParam("token")String token){
        return Results.success(userService.checkLogin(username, token));
    }

    /**
     * 用户退出登录
     */
    @DeleteMapping("/api/short-link/admin/v1/user/delete-login")
    public Result<Void> logOut(@RequestParam("username")String username, @RequestParam("token")String token){
        userService.logOut(username, token);
        return Results.success();
    }
}
