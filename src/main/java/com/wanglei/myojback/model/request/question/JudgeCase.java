package com.wanglei.myojback.model.request.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 */
@Data
public class JudgeCase implements Serializable {
    /**
     * 输入
     */
    private String input;
    /**
     * 输出
     */
    private String output;

    private static final long serialVersionUID = 1L;
}
