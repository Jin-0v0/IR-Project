package com.roadjava.doc.search.manager;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.parser.PdfParser;
import com.roadjava.doc.search.parser.ctx.ParserContext;
import com.roadjava.doc.search.service.FileService;
import com.roadjava.doc.search.service.IndexService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理
 */
@Component
public class FileManager {
    @Resource
    private IndexService indexService;
    @Autowired
    private FileService fileService;


    public ResultDTO<String> add(FileDO fileDO) {
        // 插入db
        ResultDTO<String> resultDTO = fileService.add(fileDO);
        if (resultDTO.getSuccess()) {
            // 写入lucene索引
            ResultDTO<String> writeResult = indexService.write(fileDO);
            if (!writeResult.getSuccess()) {
                List<String> paths = new ArrayList<>();
                paths.add(fileDO.getStoreRelativePath());
                fileService.deleteByPaths(paths);
                return writeResult;
            }
        }
       return resultDTO;
    }

    public ResultDTO<String> deleteByPaths(List<String> pathsToDelete) {
        ResultDTO<String> resultDTO = fileService.deleteByPaths(pathsToDelete);
        if (resultDTO.getSuccess()) {
            indexService.delete(pathsToDelete);
        }
        return resultDTO;
    }

}
