package com.wanglei.myojback.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class UserVo implements Serializable {
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
     * 创建时间
     */
    private Date createTime;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态
     */
    private Integer userStatus;



    /**
     * 用户身份
     */
    private Integer userRole;

    /**
     * 校验编号
     */
    private String acptCode;

    /**
     * 标签列表 [json]
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;

}
