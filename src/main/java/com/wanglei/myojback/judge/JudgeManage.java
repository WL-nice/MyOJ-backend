package com.wanglei.myojback.judge;

import com.wanglei.myojback.judge.codesandbox.strategy.DefaultJudgeStrategyImpl;
import com.wanglei.myojback.judge.codesandbox.strategy.JavaJudgeStrategyImpl;
import com.wanglei.myojback.judge.codesandbox.strategy.JudgeContext;
import com.wanglei.myojback.judge.codesandbox.strategy.JudgeStrategy;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.judge.codesandbox.model.JudgeInfo;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManage {
    /**
     * 判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategyImpl();
        if ("java".equals(language)) {
            judgeStrategy = new JavaJudgeStrategyImpl();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
