package com.wanglei.myojback.commmon;

import lombok.Data;

/**
 * 通用分页请求参数
 */
@Data
public class PageRequest {
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前第几页
     */
    protected int pageNum = 1;
}
