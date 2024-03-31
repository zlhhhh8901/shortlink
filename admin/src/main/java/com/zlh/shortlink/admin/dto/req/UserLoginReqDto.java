package com.zlh.shortlink.admin.dto.req;

import lombok.Data;

/**
 * 用户请求信息
 */
@Data
public class UserLoginReqDto {
    private String username;
    private String password;
}
