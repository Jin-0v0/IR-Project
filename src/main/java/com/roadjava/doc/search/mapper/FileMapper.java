package com.roadjava.doc.search.mapper;

import com.roadjava.doc.search.bean.dto.FileDTO;
import com.roadjava.doc.search.bean.entity.FileDO;

import java.util.List;

public interface FileMapper {
    int insert(FileDO fileDO);

    void deleteByPaths(List<String> pathsToDelete);

    List<FileDO> findByPage(FileDTO fileDTO);

    long selectCount(FileDTO fileDTO);
}
