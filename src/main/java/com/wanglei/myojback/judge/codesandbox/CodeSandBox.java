package com.wanglei.myojback.judge.codesandbox;

import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeRequest;
import com.wanglei.myojback.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandBox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
