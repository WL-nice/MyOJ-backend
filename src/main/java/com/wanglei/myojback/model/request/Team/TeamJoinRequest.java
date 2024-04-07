package com.wanglei.myojback.model.request.Team;


import lombok.Data;



@Data
public class TeamJoinRequest {
    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;



}


