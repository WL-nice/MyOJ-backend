package com.wanglei.myojback.judge;

import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.judge.codesandbox.CodeSandBox;
import com.wanglei.myojback.judge.codesandbox.CodeSandBoxFactory;
import com.wanglei.myojback.judge.codesandbox.CodeSandboxProxy;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeRequest;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeResponse;
import com.wanglei.myojback.judge.codesandbox.strategy.JudgeContext;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.entity.QuestionSubmit;

import com.wanglei.myojback.model.enums.QuestionSubmitStatus;
import com.wanglei.myojback.model.request.question.JudgeCase;

import com.wanglei.myojback.judge.codesandbox.model.JudgeInfo;

import com.wanglei.myojback.service.QuestionService;
import com.wanglei.myojback.service.QuestionSubmitService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private JudgeManage judgeManage;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if (questionSubmit == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "题目信息不存在");
        }
        //验证题目状态
        if (questionSubmit.getStatus() != QuestionSubmitStatus.WAITING.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "题目正在判题");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(QuestionSubmitStatus.RUNNING.getValue());
        questionSubmitUpdate.setId(questionSubmitId);
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        //在代码沙箱中运行
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandboxProxy(codeSandBox);
        String code = questionSubmit.getCode();
        String language = questionSubmit.getLanguage();
        String judgeCase = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCase, JudgeCase.class);
        List<String> input = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(input)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        List<String> output = executeCodeResponse.getOutputList();

        //设置题目判题信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(input);
        judgeContext.setOutputList(output);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);

        JudgeInfo judgeInfo = judgeManage.doJudge(judgeContext);

        //修改判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setStatus(QuestionSubmitStatus.SUCCEED.getValue());
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }

        return questionSubmitService.getById(questionSubmitId);
    }
}
