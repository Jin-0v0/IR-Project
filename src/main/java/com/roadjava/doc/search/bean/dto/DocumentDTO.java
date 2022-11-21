package com.roadjava.doc.search.bean.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 一个文档对应的对象，被lucene使用作为一个document
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
@Accessors(chain = true)
public class DocumentDTO {
    /**
     * 文件唯一标识,即: com.roadjava.doc.search.bean.entity.FileDO#id
     */
    private String id;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件内容
     */
    private String fileContent;
    /**
     * 存储路径
     */
    private String storeRelativePath;
    /**
     * 文档标题
     */
    private String fileTitle;
    /**
     * 作者
     */
    private String fileWriter;
    /**
     * 隶属关系
     */
    private String fileAffiliation;
    /**
     * 地址
     */
    private String fileAddress;
    /**
     * 日期
     */
    private String fileDate;
}
