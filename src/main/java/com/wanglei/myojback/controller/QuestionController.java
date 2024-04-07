package com.wanglei.myojback.controller;

import java.util.List;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanglei.myojback.annotation.AuthCheck;
import com.wanglei.myojback.commmon.*;
import com.wanglei.myojback.constant.CommonConstant;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.model.request.question.*;
import com.wanglei.myojback.model.vo.QuestionVO;
import com.wanglei.myojback.service.QuestionService;
import com.wanglei.myojback.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/question")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        JudgeCase judgeCase = questionAddRequest.getJudgeCase();
        if(judgeCase!=null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if(judgeConfig!=null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        question.setUserId(loginUser.getId());
        questionService.validQuestion(question, true);
        boolean result = questionService.save(question);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未发现接口");
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest, HttpServletRequest request) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(JSONUtil.toJsonStr(tags));
        }
        JudgeCase judgeCase = questionUpdateRequest.getJudgeCase();
        if(judgeCase!=null){
            question.setJudgeCase(JSONUtil.toJsonStr(judgeCase));
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if(judgeConfig!=null){
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }
        // 参数校验
//        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        if (oldQuestion == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "未发现接口");
        }
        canOperateQuestion(request, question);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     * 分页获取列表
     *
     * @param questionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionVO>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest, HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        QueryWrapper<Question> queryWrapper = questionService.getQueryWrapper(questionQueryRequest);
        Page<Question> questionPage = questionService.page(new Page<>(current,size), queryWrapper);
        Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
        return ResultUtils.success(questionVOPage);
    }

    private void canOperateQuestion(HttpServletRequest request, Question question) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        if (!Objects.equals(loginUser.getId(), question.getUserId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
    }

    @GetMapping("/get/vo")
    public BaseResponse<Question> getQuestionById(@RequestParam("id") long id,HttpServletRequest request){
        if(id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if(question==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人或管理员，不能直接获取所有信息
        if (!question.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return ResultUtils.success(question);

    }

}