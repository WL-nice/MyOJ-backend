package com.wanglei.myojback.judge.codesandbox.strategy;

import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.request.question.JudgeCase;
import com.wanglei.myojback.judge.codesandbox.model.JudgeInfo;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private Question question;

    private QuestionSubmit questionSubmit;

}
