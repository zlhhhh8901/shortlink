package com.zlh.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 更改用户信息请求参数
 */
@Data
public class UserUpdateReqDTO {
    private Long id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
