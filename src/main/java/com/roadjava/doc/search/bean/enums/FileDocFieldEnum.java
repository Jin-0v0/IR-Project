package com.roadjava.doc.search.bean.enums;

/**
 * 文件对应的索引doc对象包含的域的名字
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
public enum FileDocFieldEnum {

    NAME("fileName","文件名field,分词"),
    CONT("cont","文件内容field,分词"),
    RELATIVE_PATH("path","文件存储路径,不分词,一个文件的唯一标识"),
    TITL("title","标题,分词"),
    WRIT("writer","文件作者，分词"),
    DATE("date","文件时间，不分词"),
    AFFI("affiliation","文件隶属关系，分词"),
    ADDR("address","文件地址，不分");

    private String name;
    private String desc;
    FileDocFieldEnum(String name,String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }
}
