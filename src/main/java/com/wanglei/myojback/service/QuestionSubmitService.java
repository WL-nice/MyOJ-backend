package com.wanglei.myojback.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitAddRequest;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitQueryRequest;
import com.wanglei.myojback.model.vo.QuestionSubmitVO;
import jakarta.servlet.http.HttpServletRequest;

public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionsubmitQueryRequest);

    public Page<QuestionSubmitVO> getQuestionVOPage(Page<QuestionSubmit> questionPage, HttpServletRequest request);
}
