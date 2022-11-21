package com.roadjava.doc.search.bean.dto;

import com.roadjava.doc.search.util.Constants;
import lombok.Data;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public abstract class BaseDTO {
    /**
     * 当前是第几页
     */
    private Integer pageNow;
    /**
     * 每页展示多少条
     */
    private Integer pageSize;

    /**
     * limit 0,10
     * @return
     */
    public Integer getStart() {
        if (getPageNow() == null) {
            return null;
        }
        return (getPageNow() - 1) * getPageSize();
    }

    public Integer getPageSize() {
        return pageSize != null ? pageSize : Constants.PAGE_SIZE;
    }
}
