package com.wanglei.myojback.model.enums;

/**
 * 题目提交状态枚举类
 */
public enum QuestionSubmitStatus {
    WAITING(0, "待判题"),
    RUNNING(1, "判题中"),
    SUCCEED(2, "通过"),
    FAILED(3, "错误"),
    ;

    private int value;
    private String text;

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static QuestionSubmitStatus getQuestionSubmitStatusByValue(Integer value) {
        if (value == null) {
            return null;
        }
        QuestionSubmitStatus[] values = QuestionSubmitStatus.values();
        for (QuestionSubmitStatus questionSubmitStatus : values) {
            if (value == questionSubmitStatus.getValue()) {
                return questionSubmitStatus;
            }
        }
        return null;
    }

    QuestionSubmitStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
