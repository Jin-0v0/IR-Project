package com.roadjava.doc.search.parser;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.enums.FileTypeEnum;
import com.roadjava.doc.search.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 解析文本文件
 */
@Component
@Slf4j
public class TxtParser extends Parser{

    @Override
    protected String getFileCont(File file) throws Exception {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    protected String getFileTitl(File file) throws Exception {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    protected String getFileWrit(File file) throws Exception {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    protected String getFileDate(File file) throws Exception {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    protected String getFileAffi(File file) throws Exception {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    protected String getFileAddr(File file) throws Exception {
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
    }

    @Override
    public String getHandleType() {
        return FileTypeEnum.TXT.getTypeName();
    }
}
