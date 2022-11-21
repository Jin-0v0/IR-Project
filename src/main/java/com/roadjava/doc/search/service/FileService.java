package com.roadjava.doc.search.service;

import com.roadjava.doc.search.bean.dto.FileDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.bean.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {
    ResultDTO<String> add(FileDO fileDO);


    ResultDTO<String> deleteByPaths(List<String> pathsToDelete);

    ResultDTO<List<FileVO>> findByPage(FileDTO fileDTO);

    String upload(MultipartFile multipartFile);

}
