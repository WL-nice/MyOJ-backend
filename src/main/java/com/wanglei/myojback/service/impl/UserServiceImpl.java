package com.wanglei.myojback.service.impl;

import java.util.*;


import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.constant.CommonConstant;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.request.User.UserQueryRequest;
import com.wanglei.myojback.model.request.User.UserUpdateRequest;
import com.wanglei.myojback.model.vo.QuestionVO;
import com.wanglei.myojback.model.vo.UserVo;
import com.wanglei.myojback.service.UserService;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.mapper.UserMapper;
import com.wanglei.myojback.util.AlgorithmUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.apache.commons.math3.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.wanglei.myojback.constant.UserConstant.ADMIN_ROLE;
import static com.wanglei.myojback.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author master
 * 用户服务实现类
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "muqiu";


    @Override
    public long UserRegister(String userAccount, String userPassword, String checkPassword) {
        //1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {

            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能包含特殊字符");
        }
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不同");
        }
        //账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已注册");
        }

        //校验编号不能重复
//        queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("acptCode", acptCode);
//        count = userMapper.selectCount(queryWrapper);
//        if (count > 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
//        }

        //2、加密
        String entryptfPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(entryptfPassword);
//        user.setAcptCode(acptCode);
        user.setTags("[]");
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1、校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码不能为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8位");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        //2、加密
        String entryptfPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", entryptfPassword);
        User user = userMapper.selectOne(queryWrapper);
        //
        if (user == null) {
            log.info("user login failed,UserAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码错误");
        }


        //3、脱敏
        User safetUser = getSafetUser(user);
//        UserVo userVO = getUserVO(user);
        //4、记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 用户脱敏
     *
     * @param user 用户
     * @return 脱敏后的信息
     */
    @Override
    public User getSafetUser(User user) {
        User safetUser = new User();
        safetUser.setId(user.getId());
        safetUser.setUsername(user.getUsername());
        safetUser.setUserAccount(user.getUserAccount());
        safetUser.setAvatarUrl(user.getAvatarUrl());
        safetUser.setGender(user.getGender());
        safetUser.setPhone(user.getPhone());
        safetUser.setEmail(user.getEmail());
        safetUser.setAcptCode(user.getAcptCode());
        safetUser.setUserRole(user.getUserRole());
        safetUser.setUserStatus(user.getUserStatus());
        safetUser.setCreateTime(user.getCreateTime());
        safetUser.setTags(user.getTags());
        return safetUser;

    }

    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;

    }

    /**
     * 根据标签搜索用户
     *
     * @param tagNamelist 用户标签
     * @return
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNamelist) {
        if (CollectionUtils.isEmpty(tagNamelist)) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        //拼接and查询
        for (String tagName : tagNamelist) {
            queryWrapper = queryWrapper.like("tags", tagName);

        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetUser).collect(Collectors.toList());


    }

    @Override
    public Integer updateUser(UserUpdateRequest userUpdateRequest, User loginUser) {
        long id = userUpdateRequest.getId();
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果为管理员，允许修改
        //如果不是，只允许修改自身的信息
        if (!isAdmin(loginUser) && !Objects.equals(userUpdateRequest.getId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(id);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);
        List<String> tags = userUpdateRequest.getTags();
        if(tags!=null){
            user.setTags(JSONUtil.toJsonStr(tags));
        }
        return userMapper.updateById(user);

    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        List<User> userList = this.list(queryWrapper);
        String tags = loginUser.getTags();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下标 => 相似度
        List<Pair<User, Long>> list = new ArrayList<>();
        // 依次计算所有用户和当前用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String userTags = user.getTags();
            // 无标签或者为当前用户自己
            if (StringUtils.isBlank(userTags) || Objects.equals(user.getId(), loginUser.getId())) {
                continue;
            }
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            // 计算分数
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        // 按编辑距离由小到大排序
        List<Pair<User, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        // 原本顺序的 userId 列表
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getKey().getId()).collect(Collectors.toList());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userIdList);
        // 1, 3, 2
        // User1、User2、User3
        // 1 => User1, 2 => User2, 3 => User3
        Map<Long, List<User>> userIdUserListMap = this.list(userQueryWrapper)
                .stream()
                .map(this::getSafetUser)
                .collect(Collectors.groupingBy(User::getId));
        List<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }

    @Override
    public UserVo getUserVO(User user) {
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        String tags = user.getTags();
        if (StringUtils.isNotBlank(tags)) {
            userVo.setTags(JSONUtil.toList(tags, String.class));
        }
        return userVo;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        Long id = userQueryRequest.getId();
        String username = userQueryRequest.getUsername();
        String userAccount = userQueryRequest.getUserAccount();
        String phone = userQueryRequest.getPhone();
        String email = userQueryRequest.getEmail();
        List<String> tags = userQueryRequest.getTags();
        String sortOrder = userQueryRequest.getSortOrder();
        String sortField = userQueryRequest.getSortField();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(id != null && id > 0, "id", id);

        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    @Override
    public Page<UserVo> getUserVOPage(Page<User> userPage, HttpServletRequest request) {
        List<User> userList = userPage.getRecords();
        Page<UserVo> userVoPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isEmpty(userList)) {
            return userVoPage;
        }
        // 填充信息
        List<UserVo> userVOList = userList.stream().map(user -> {
            UserVo userVO = getUserVO(user);
            return userVO;
        }).collect(Collectors.toList());
        userVoPage.setRecords(userVOList);
        return userVoPage;
    }


    /**
     * 判断是否为管理员
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }


    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        return (User) userObj;
    }


}




