package com.roadjava.doc.search.parser.ctx;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.enums.FileTypeEnum;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.parser.Parser;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Component
public class ParserContext {
    @Resource
    private List<Parser> parsers;
    private Map<String,Parser> map = new HashMap<>();
    @PostConstruct
    public void init() {
        parsers.forEach(parser -> map.put(parser.getHandleType(),parser));
    }


    public ResultDTO<DocumentDTO> parse(FileDO fileDO) {
        Parser appropriateParser = findAppropriateParser(fileDO);
        return appropriateParser.parse(fileDO);
    }

    /**
     * 找到合适的parser
     * @return
     */
    private Parser findAppropriateParser(FileDO fileDO) {
        String extension = FilenameUtils.getExtension(fileDO.getOriginalFileName()).toLowerCase();
        if (map.get(extension) != null){
            return map.get(extension);
        }
        // 默认返回文本类型的
        return map.get(FileTypeEnum.TXT.getTypeName());
    }
}
