package com.roadjava.doc.search.bean.req;

import com.roadjava.doc.search.bean.enums.FileDocFieldEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 搜索入参对象
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public class SearchRequest {
    /**
     * 搜索域
     * @see FileDocFieldEnum#NAME 的name:文件名
     * @see FileDocFieldEnum#CONT 的name :文件内容
     */
    @NotBlank
    private String searchField;
    /**
     * 原始搜索词
     */
    @NotBlank
    private String searchWord;
}
