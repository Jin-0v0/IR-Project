package com.roadjava.doc.search.config;

import com.roadjava.doc.search.util.CheckUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * lucene索引相关配置
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
@ConfigurationProperties(prefix = "lucene.idx")
public class LuceneProperties {
    /**
     * 索引文件存放目录，文件夹的绝对路径
     */
    private String dir;
    /**
     * 最大搜索出的条数限制
     */
    private int limit = 1024;
    /**
     * 摘要长度
     */
    private int summaryLength = 300;
    /**
     * 高亮html前置标签
     */
    private String preTag = "<b><font color='red'>";
    /**
     * 高亮html后置标签
     */
    private String postTag = "</font></b>";
    /**
     * 是否采用近实时的方式进行检索
     */
    private Boolean useNRT = false;

    @PostConstruct
    public void init() {
        CheckUtil.checkDir(dir);
    }
}
