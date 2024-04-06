package com.wanglei.myojback.model.domain.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    /**
     * 用户id
     */

    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 [json]
     */
    private String tags;
}
