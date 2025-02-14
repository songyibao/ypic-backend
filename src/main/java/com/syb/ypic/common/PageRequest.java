package com.syb.ypic.common;

import com.syb.ypic.constant.OrderByConstant;
import lombok.Data;

/**
 * 分页请求
 */
@Data
public class PageRequest {
    /**
     * 当前页号
     */
    private int current = 1;
    /**
     * 每页大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式(默认降序)
     */
    private String sortOrder = OrderByConstant.DESC;
}
