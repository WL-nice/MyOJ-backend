package com.wanglei.myojback.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.request.question.QuestionQueryRequest;
import com.wanglei.myojback.model.vo.QuestionVO;
import jakarta.servlet.http.HttpServletRequest;

public interface QuestionService extends IService<Question> {
    void validQuestion(Question question, boolean b);

    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    QuestionVO getQuestionVO(Question question);
}
