package com.wanglei.myojback.model.request.question;

import lombok.Data;

/**
 * 题目用例
 */
@Data
public class JudgeCase {
    /**
     * 输入
     */
    private String input;
    /**
     * 输出
     */
    private String output;
}
