package com.wanglei.myojback.judge.codesandbox.impI;

import com.wanglei.myojback.judge.codesandbox.CodeSandBox;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeRequest;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeResponse;
import com.wanglei.myojback.model.enums.JudgeInfoMessage;
import com.wanglei.myojback.model.enums.QuestionSubmitStatus;
import com.wanglei.myojback.judge.codesandbox.model.JudgeInfo;

import java.util.List;

/**
 * 示例沙箱
 */
public class ExampleCodeSandBoxImpl implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatue(QuestionSubmitStatus.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessage.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;

    }
}
