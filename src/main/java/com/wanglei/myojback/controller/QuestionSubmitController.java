package com.wanglei.myojback.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wanglei.myojback.commmon.BaseResponse;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.commmon.ResultUtils;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitAddRequest;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitQueryRequest;
import com.wanglei.myojback.model.vo.QuestionSubmitVO;
import com.wanglei.myojback.service.QuestionSubmitService;
import com.wanglei.myojback.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController //适用于编写restful风格的API，返回值默认为json类型
@RequestMapping("/questionSubmit")
@CrossOrigin(origins = "http://localhost:8080", allowCredentials = "true")
@Slf4j
public class QuestionSubmitController {
    @Resource
    QuestionSubmitService questionSubmitService;

    @Resource
    UserService userService;

    /**
     * 提交
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        Long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);


    }

    /**
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest, HttpServletRequest request) {
        if (questionSubmitQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        QueryWrapper<QuestionSubmit> queryWrapper = questionSubmitService.getQueryWrapper(questionSubmitQueryRequest);
        Page<QuestionSubmit> questionPage = questionSubmitService.page(new Page<>(current, size), queryWrapper);
        Page<QuestionSubmitVO> questionSubmitVOPage = questionSubmitService.getQuestionVOPage(questionPage, request);
        return ResultUtils.success(questionSubmitVOPage);
    }
}
