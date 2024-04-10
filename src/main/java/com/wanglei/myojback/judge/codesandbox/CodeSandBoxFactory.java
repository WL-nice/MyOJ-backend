package com.wanglei.myojback.judge.codesandbox;

import com.wanglei.myojback.judge.codesandbox.impI.ExampleCodeSandBoxImpl;
import com.wanglei.myojback.judge.codesandbox.impI.RemoteCodeSandBoxImpl;
import com.wanglei.myojback.judge.codesandbox.impI.ThirdPartyCodeSandBoxImpl;

public class CodeSandBoxFactory {

    public static CodeSandBox newInstance(String type) {
        return switch (type) {
            case ("remote") -> new RemoteCodeSandBoxImpl();
            case ("thirdParty") -> new ThirdPartyCodeSandBoxImpl();
            default -> new ExampleCodeSandBoxImpl();
        };
    }
}
