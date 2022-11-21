package com.roadjava.doc.search.util;

import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
public class CheckUtil {

    /**
     * 对目录进行校验
     */
    public static void checkDir(String dir){
        if (!StringUtils.hasText(dir)) {
            throw new RuntimeException("目录未配置");
        }
        File dirFile = new File(dir);
        if (!dirFile.isAbsolute()) {
            throw new RuntimeException("目录不是绝对路径");
        }
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
    }

}
