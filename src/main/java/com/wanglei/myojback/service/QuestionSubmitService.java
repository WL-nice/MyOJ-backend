package com.wanglei.myojback.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitAddRequest;

public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);
}
