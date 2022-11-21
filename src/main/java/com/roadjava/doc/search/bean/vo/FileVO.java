package com.roadjava.doc.search.bean.vo;

import lombok.Data;

/**
 * 前端搜索展示的条目
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public class FileVO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 单位:B,文件大小
     */
    private Long size;
    /**
     * 文件名
     */
    private String originalFileName;
    /**
     * 存储路径
     */
    private String storeRelativePath;
    /**
     * 存储绝对路径
     */
    private String storeAbsPath;
    /**
     * 摘要
     */
    private String summary;
}
