package com.roadjava.doc.search.bean.res;

import com.roadjava.doc.search.util.Constants;
import lombok.Data;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public class TableResult<T> {
    /**
     * 查询出的记录
     */
    private List<T> rows;
    /**
     * 总条数
     */
    private long totalCount;

    /**
     * 总的页数
     * totalCount:91
     * pageSize:10
     * ==== 10页
     * @return
     */
    public long getPageCount() {
        long pageCount;
        if (totalCount % Constants.PAGE_SIZE == 0) {
            pageCount = totalCount / Constants.PAGE_SIZE;
        } else {
            pageCount = totalCount / Constants.PAGE_SIZE + 1;
        }
        return pageCount;
    }
}
