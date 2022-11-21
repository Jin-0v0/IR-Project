package com.roadjava.doc.search.bean.enums;

/**
 * 可以处理的文件类型
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
public enum FileTypeEnum {
    /**
     * pdf类型的文件
     */
    PDF,
    /**
     * 文本类型的文件,如.txt、.json、.html等文本类型的文件
     */
    TXT,
    /**
     * 非文本类型的文件,如.xml等文本类型的文件
     */
    XML;

    public String getTypeName() {
        return this.name().toLowerCase();
    }
}
