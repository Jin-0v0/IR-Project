package com.roadjava.doc.search.config;

import com.roadjava.doc.search.util.CheckUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * 上传配置
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Configuration
@ConfigurationProperties(prefix = "upload")
@Data
public class UploadProperties {
    /**
     * 本地绝对路径,存储上传的文件
     */
    private String dir;
    @PostConstruct
    public void init() {
        CheckUtil.checkDir(dir);
    }
}
