package com.roadjava.doc.search.parser;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.enums.FileTypeEnum;
import com.roadjava.doc.search.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
@Slf4j
public class PdfParser extends Parser{

    @Override
    protected String getFileCont(File file) throws Exception{
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 总页数
            int numberOfPages = pdfDocument.getNumberOfPages();
            // 默认不排序
            stripper.setSortByPosition(true);
            // 默认就是从第1页开始读
            stripper.setStartPage(1);
            stripper.setEndPage(numberOfPages);
            // 从PDF文档对象中剥离文本
            String fileContent = stripper.getText(pdfDocument);
            log.info("文件:{}读取到的内容:{}",file.getName(),fileContent);
            return fileContent;
        }
    }

    @Override
    protected String getFileWrit(File file) throws Exception{
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 总页数
            int numberOfPages = pdfDocument.getNumberOfPages();
            // 默认不排序
            stripper.setSortByPosition(true);
            // 默认就是从第1页开始读
            stripper.setStartPage(1);
            stripper.setEndPage(numberOfPages);
            // 从PDF文档对象中剥离文本
            String fileContent = stripper.getText(pdfDocument);
            log.info("文件:{}读取到的内容:{}",file.getName(),fileContent);
            return fileContent;
        }
    }

    @Override
    protected String getFileDate(File file) throws Exception{
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 总页数
            int numberOfPages = pdfDocument.getNumberOfPages();
            // 默认不排序
            stripper.setSortByPosition(true);
            // 默认就是从第1页开始读
            stripper.setStartPage(1);
            stripper.setEndPage(numberOfPages);
            // 从PDF文档对象中剥离文本
            String fileContent = stripper.getText(pdfDocument);
            log.info("文件:{}读取到的内容:{}",file.getName(),fileContent);
            return fileContent;
        }
    }

    @Override
    protected String getFileAffi(File file) throws Exception{
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 总页数
            int numberOfPages = pdfDocument.getNumberOfPages();
            // 默认不排序
            stripper.setSortByPosition(true);
            // 默认就是从第1页开始读
            stripper.setStartPage(1);
            stripper.setEndPage(numberOfPages);
            // 从PDF文档对象中剥离文本
            String fileContent = stripper.getText(pdfDocument);
            log.info("文件:{}读取到的内容:{}",file.getName(),fileContent);
            return fileContent;
        }
    }

    @Override
    protected String getFileAddr(File file) throws Exception{
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 总页数
            int numberOfPages = pdfDocument.getNumberOfPages();
            // 默认不排序
            stripper.setSortByPosition(true);
            // 默认就是从第1页开始读
            stripper.setStartPage(1);
            stripper.setEndPage(numberOfPages);
            // 从PDF文档对象中剥离文本
            String fileContent = stripper.getText(pdfDocument);
            log.info("文件:{}读取到的内容:{}",file.getName(),fileContent);
            return fileContent;
        }
    }

    @Override
    protected String getFileTitl(File file) throws Exception{
        try (PDDocument pdfDocument = PDDocument.load(file)) {
            // 新建一个PDF文本剥离器
            PDFTextStripper stripper = new PDFTextStripper();
            // 总页数
            int numberOfPages = pdfDocument.getNumberOfPages();
            // 默认不排序
            stripper.setSortByPosition(true);
            // 默认就是从第1页开始读
            stripper.setStartPage(1);
            stripper.setEndPage(numberOfPages);
            // 从PDF文档对象中剥离文本
            String fileContent = stripper.getText(pdfDocument);
            log.info("文件:{}读取到的内容:{}",file.getName(),fileContent);
            return fileContent;
        }
    }

    @Override
    public String getHandleType() {
        return FileTypeEnum.PDF.getTypeName();
    }
}
