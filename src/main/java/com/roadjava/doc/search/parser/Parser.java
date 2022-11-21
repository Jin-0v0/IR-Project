package com.roadjava.doc.search.parser;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Slf4j
public abstract class Parser {
    @Resource
    private UploadProperties uploadProperties;
    /**
     * 模板方法
     * 解析为lucene存储的中间文档对象
     * @param fileDO
     * @return
     */
    public ResultDTO<DocumentDTO> parse(FileDO fileDO){
        String fileAbsPath = uploadProperties.getDir() + fileDO.getStoreRelativePath();
        File file = new File(fileAbsPath);
        try {
            String fileCont = getFileCont(file);
            String fileWrit = getFileWrit(file);
            String fileDate = getFileDate(file);
            String fileAffi = getFileAffi(file);
            String fileAddr = getFileAddr(file);
            String fileTitl = getFileTitl(file);
            if (fileCont == null) {
                return ResultDTO.buildFailure("读取不到内容");
            }
            fileCont = fileCont.replaceAll("[\\n|\\r\\n|\\r]","");
            // 去掉换行,如 聊\r\n天,导致搜"聊天"搜不到
            if (!StringUtils.hasText(fileCont)) {
                return ResultDTO.buildFailure("读取文件不到内容");
            }
            DocumentDTO dto = new DocumentDTO()
                    .setId(String.valueOf(fileDO.getId()))
                    .setFileName(fileDO.getOriginalFileName())
                    .setFileContent(fileCont)
                    .setStoreRelativePath(fileDO.getStoreRelativePath())
                    .setFileAddress(fileAddr)
                    .setFileTitle(fileTitl)
                    .setFileWriter(fileWrit)
                    .setFileAffiliation(fileAffi)
                    .setFileDate(fileDate);
            return ResultDTO.buildSuccess(dto);
        } catch (Exception e) {
            log.error("解析文件失败,文件路径:{}", fileAbsPath, e);
        }
        return ResultDTO.buildFailure("解析文件失败");
    }

    /**
     * 获取文件内容
     * @param file
     * @return
     * @throws Exception
     */
    protected abstract String getFileCont(File file)  throws Exception;
    protected abstract String getFileTitl(File file)  throws Exception;
    protected abstract String getFileWrit(File file)  throws Exception;
    protected abstract String getFileDate(File file)  throws Exception;
    protected abstract String getFileAffi(File file)  throws Exception;
    protected abstract String getFileAddr(File file)  throws Exception;

    public abstract String getHandleType();
}
