package com.roadjava.doc.search.service;

import com.roadjava.doc.search.bean.dto.DocumentDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.req.SearchRequest;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.bean.vo.FileVO;

import java.util.List;

public interface IndexService {
    /**
     * 写数据到索引中
     * @param fileDO
     */
    ResultDTO<String> write(FileDO fileDO);
    /**
     * 根据文件标识删除索引
     */
    void delete(List<String> pathsToDelete);
    /**
     * 搜索文献
     * @param request 请求参数
     * @return
     */
    ResultDTO<List<FileVO>> search(SearchRequest request);
}
