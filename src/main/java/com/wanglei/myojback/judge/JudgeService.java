package com.wanglei.myojback.judge;

import com.wanglei.myojback.model.entity.QuestionSubmit;


/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     *
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
