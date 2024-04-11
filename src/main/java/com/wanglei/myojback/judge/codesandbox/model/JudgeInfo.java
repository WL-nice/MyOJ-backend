package com.wanglei.myojback.judge.codesandbox.model;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {
    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗时间（ms）
     */
    private long time;

    /**
     * 消耗内存（kb）
     */
    private long memory;
}
