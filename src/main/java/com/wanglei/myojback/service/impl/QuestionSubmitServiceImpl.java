package com.wanglei.myojback.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.mapper.QuestionSubmitMapper;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.model.enums.QuestionSubmitLanguage;
import com.wanglei.myojback.model.enums.QuestionSubmitStatus;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitAddRequest;
import com.wanglei.myojback.service.QuestionService;
import com.wanglei.myojback.service.QuestionSubmitService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    QuestionService questionService;

    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguage enumByValue = QuestionSubmitLanguage.getEnumByValue(language);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无法识别语言，请用java，c++，golang");
        }
        Question question = questionService.getById(questionSubmitAddRequest.getQuestionId());
        if (question == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目不存在");
        }
        Long userId = loginUser.getId();

        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionSubmit.getQuestionId());
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        questionSubmit.setStatus(QuestionSubmitStatus.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据保存失败");
        }
        return questionSubmit.getId();
    }
}
