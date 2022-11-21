package com.roadjava.doc.search.bean.entity;

import lombok.Data;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public class FileDO {
    /**
     * 主键
     */
    private Long id;
    /**
     * 文件原始名字
     */
    private String originalFileName;
    /**
     * 单位:B,文件大小
     */
    private Long size;
    /**
     * 存储路径: com.roadjava.doc.search.config.UploadProperties#dir
     *  + storeRelativePath = 文件绝对路径
     */
    private String storeRelativePath;
    private String fileTitle;
    private String fileWriter;
    private String fileAffiliation;
    private String fileDate;
    private String fileAddress;
}
