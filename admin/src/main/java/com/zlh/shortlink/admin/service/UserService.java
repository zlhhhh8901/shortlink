package com.zlh.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zlh.shortlink.admin.dao.entity.UserDO;
import com.zlh.shortlink.admin.dto.req.UserLoginReqDto;
import com.zlh.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.zlh.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.zlh.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.zlh.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户接口层
 */
public interface UserService extends IService<UserDO> {
    /**
     * 根据用户名查询用户信息
     * @param username 用户名
     * @return 用户返回实体
     */
    UserRespDTO getUserByUsername(String username);

    /**
     * 查询用户是否存在
     * @param username 用户名
     * @return 存在返回true
     */
    Boolean hasUserName(String username);

    /**
     * 注册用户
     * @param requestParam 注册用户请求参数
     */
    void register(UserRegisterReqDTO requestParam);

    /**
     * 根据用户名修改用户信息
     * @param updateReqDTO 修改用户请求参数
     */
    void update(UserUpdateReqDTO updateReqDTO);

    /**
     * 用户登录
     * @param requestDto 用户登录请求参数
     * @return 用户登录返回参数
     */
    UserLoginRespDTO login(UserLoginReqDto requestDto);

    /**
     * 检测用户是否登录
     * @param username 用户名
     * @param token 用户登录 token
     * @return 登录过则返回 true
     */
    Boolean checkLogin(String username, String token);
}
