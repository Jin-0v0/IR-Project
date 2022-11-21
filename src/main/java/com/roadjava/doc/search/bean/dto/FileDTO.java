package com.roadjava.doc.search.bean.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public class FileDTO extends BaseDTO{
    private List<String> pathsToDelete;
    /**
     * 前端传来的文件原始名字,逗号分隔
     */
    private String originalFileNames;
    /**
     * 单位:B,前端传来的文件大小,逗号分隔
     */
    private String sizes;
}
