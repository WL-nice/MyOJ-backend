package com.wanglei.myojback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanglei.myojback.mapper.UserTeamMapper ;
import com.wanglei.myojback.model.domain.UserTeam;
import com.wanglei.myojback.service.UserTeamService ;
import org.springframework.stereotype.Service;

/**
* @author admin
* @description 针对表【user_team(用户-队伍)】的数据库操作Service实现
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




