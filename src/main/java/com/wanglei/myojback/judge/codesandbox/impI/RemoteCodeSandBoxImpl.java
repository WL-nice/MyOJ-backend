package com.wanglei.myojback.judge.codesandbox.impI;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.wanglei.myojback.commmon.ErrorCode;
import com.wanglei.myojback.exception.BusinessException;
import com.wanglei.myojback.judge.codesandbox.CodeSandBox;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeRequest;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱（调用接口）
 */
public class RemoteCodeSandBoxImpl implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);

    }

}

