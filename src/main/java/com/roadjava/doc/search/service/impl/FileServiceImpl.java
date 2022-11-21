package com.roadjava.doc.search.service.impl;

import com.roadjava.doc.search.bean.dto.FileDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.bean.vo.FileVO;
import com.roadjava.doc.search.config.UploadProperties;
import com.roadjava.doc.search.mapper.FileMapper;
import com.roadjava.doc.search.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private FileMapper fileMapper;
    @Resource
    private UploadProperties uploadProperties;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<String> add(FileDO fileDO) {
        int result = fileMapper.insert(fileDO);
        if (result == 1) {
            return ResultDTO.buildSuccess("存入数据库成功");
        } else {
            return ResultDTO.buildSuccess("存入数据库失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<String> deleteByPaths(List<String> pathsToDelete) {
        fileMapper.deleteByPaths(pathsToDelete);
        return ResultDTO.buildSuccess("删除成功");
    }

    @Override
    public ResultDTO<List<FileVO>> findByPage(FileDTO fileDTO) {
        List<FileDO> dos = fileMapper.findByPage(fileDTO);
        if (dos.isEmpty()) {
            return ResultDTO.buildSuccess(Collections.emptyList(),0L);
        }
        // 附加上路径前缀
        List<FileVO> fileVOS = dos.stream().map(fileDO -> {
            FileVO fileVO = new FileVO();
            fileVO.setId(fileDO.getId());
            fileVO.setOriginalFileName(fileDO.getOriginalFileName());
            fileVO.setSize(fileDO.getSize());
            fileVO.setStoreRelativePath(fileDO.getStoreRelativePath());
            fileVO.setStoreAbsPath(uploadProperties.getDir() + fileDO.getStoreRelativePath());
            return fileVO;
        }).collect(Collectors.toList());
        long total = fileMapper.selectCount(fileDTO);
        return ResultDTO.buildSuccess(fileVOS, total);
    }

    @Override
    public String upload(MultipartFile multipartFile) {
        // 重命名文件
        String newFileName = reName(multipartFile.getOriginalFilename());
        String absPathToStore = uploadProperties.getDir() + newFileName;
        // 进行文件存储
        doStoreFile(absPathToStore, multipartFile);
        // 直接存储在根目录下,返回文件名即可
        return newFileName;
    }

    private void doStoreFile(String absPathToStore, MultipartFile multipartFile) {
        File destFile = new File(absPathToStore);
        // 如果父级目录不存在，就创建
        File parentFile = destFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        // 执行文件存储
        try {
            multipartFile.transferTo(destFile);
        } catch (IOException e) {
            log.error("存储文件出错", e);
            throw new RuntimeException("存储文件出错");
        }

    }

    /**
     * 获取新的文件名
     *
     * @param originalFilename 小帽.jpg
     * @return 123.jpg
     */
    private String reName(String originalFilename) {
        // 避免同一时刻文件过多导致重名
        DecimalFormat decimalFormat = new DecimalFormat("000000");
        String format = decimalFormat.format(Math.ceil(Math.random() * 100000));
        return System.nanoTime() + format + "." + FilenameUtils.getExtension(originalFilename);
    }
}
