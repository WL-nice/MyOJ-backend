package com.wanglei.myojback.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.constant.CommonConstant;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.judge.JudgeService;
import com.wanglei.myojback.mapper.QuestionSubmitMapper;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.entity.User;
import com.wanglei.myojback.model.enums.QuestionSubmitLanguage;
import com.wanglei.myojback.model.enums.QuestionSubmitStatus;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitAddRequest;
import com.wanglei.myojback.model.request.questionsubmit.QuestionSubmitQueryRequest;
import com.wanglei.myojback.model.vo.QuestionSubmitVO;
import com.wanglei.myojback.model.vo.QuestionVO;
import com.wanglei.myojback.service.QuestionService;
import com.wanglei.myojback.service.QuestionSubmitService;
import com.wanglei.myojback.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    QuestionService questionService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private UserService userService;

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
        questionSubmit.setQuestionId(questionSubmitAddRequest.getQuestionId());
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        questionSubmit.setStatus(QuestionSubmitStatus.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        question.setSubmitNum(question.getSubmitNum() + 1);
        questionService.updateById(question);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据保存失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        //执行判题服务
//        judgeService.doJudge(questionSubmitId);
        CompletableFuture.runAsync(() -> {
            judgeService.doJudge(questionSubmitId);
        });
        return questionSubmitId;
    }

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionsubmitQueryRequest) {
        String language = questionsubmitQueryRequest.getLanguage();
        Integer status = questionsubmitQueryRequest.getStatus();
        Long questionId = questionsubmitQueryRequest.getQuestionId();
        Long userId = questionsubmitQueryRequest.getUserId();
        int pageSize = questionsubmitQueryRequest.getPageSize();
        if (pageSize > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String sortOrder = questionsubmitQueryRequest.getSortOrder();
        String sortField = questionsubmitQueryRequest.getSortField();
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(questionId != null && questionId > 0, "questionId", questionId);
        queryWrapper.eq(userId != null && userId > 0, "userId", userId);
        queryWrapper.eq(status != null && status < 3 && status >= 0, "status", status);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;

    }

    @Override
    public Page<QuestionSubmitVO> getQuestionVOPage(Page<QuestionSubmit> questionPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionPage.getCurrent(), questionPage.getSize(), questionPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(question -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(question);
            Long userId = question.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionSubmitVO.setUserVO(userService.getUserVO(user));
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
}
