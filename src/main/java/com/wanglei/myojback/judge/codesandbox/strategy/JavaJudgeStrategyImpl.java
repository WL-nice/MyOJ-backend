package com.wanglei.myojback.judge.codesandbox.strategy;

import cn.hutool.json.JSONUtil;
import com.wanglei.myojback.model.entity.Question;
import com.wanglei.myojback.model.entity.QuestionSubmit;
import com.wanglei.myojback.model.enums.JudgeInfoMessage;
import com.wanglei.myojback.model.request.question.JudgeCase;
import com.wanglei.myojback.model.request.question.JudgeConfig;
import com.wanglei.myojback.judge.codesandbox.model.JudgeInfo;

import java.util.List;

/**
 * Java 默认判题策略
 */
public class JavaJudgeStrategyImpl implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> input = judgeContext.getInputList();
        List<String> output = judgeContext.getOutputList();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Question question = judgeContext.getQuestion();
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();

        long time = judgeInfo.getTime();
        long memory = judgeInfo.getMemory();
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setTime(time);
        judgeInfoResponse.setMemory(memory);
        JudgeInfoMessage judgeInfoMessage = JudgeInfoMessage.ACCEPTED;

        if (output.size() != input.size()) {
            judgeInfoMessage = JudgeInfoMessage.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        //判断输出和预测输出是否相等
        for (int i = 0; i < judgeCaseList.size(); i++) {
            JudgeCase judgeCase1 = judgeCaseList.get(i);
            if (!judgeCase1.getOutput().equals(output.get(i))) {
                judgeInfoMessage = JudgeInfoMessage.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
                return judgeInfoResponse;
            }
        }
        //判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if (memory > needMemoryLimit) {
            judgeInfoMessage = JudgeInfoMessage.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        // Java程序本身需要额外执行时间 假设要10秒
        long JAVA_PROGRAM_COST_TIME = 10000L;
        if ((time - JAVA_PROGRAM_COST_TIME) > needTimeLimit) {
            judgeInfoMessage = JudgeInfoMessage.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
            return judgeInfoResponse;
        }
        judgeInfoResponse.setMessage(judgeInfoMessage.getValue());
        return judgeInfoResponse;


    }
}
