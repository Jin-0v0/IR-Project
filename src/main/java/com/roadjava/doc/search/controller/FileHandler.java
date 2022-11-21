package com.roadjava.doc.search.controller;

import com.roadjava.doc.search.bean.dto.FileDTO;
import com.roadjava.doc.search.bean.entity.FileDO;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.bean.res.TableResult;
import com.roadjava.doc.search.bean.vo.FileVO;
import com.roadjava.doc.search.manager.FileManager;
import com.roadjava.doc.search.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manage")
@Slf4j
public class FileHandler {
    @Autowired
    private FileService fileService;
    @Resource
    private FileManager fileManager;


    @GetMapping(value = {"/", ""})
    public String toIndex() {
        return "backend/list";
    }

    @PostMapping("/findByPage")
    @ResponseBody
    public ResultDTO<TableResult<FileVO>> findByPage(FileDTO fileDTO) {
        try {
            TableResult<FileVO> tableResult = new TableResult<>();
            ResultDTO<List<FileVO>> resultDTO = fileService.findByPage(fileDTO);
            tableResult.setRows(resultDTO.getData());
            tableResult.setTotalCount(resultDTO.getTotal());
            return ResultDTO.buildSuccess(tableResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultDTO.buildFailure("查询失败");
    }

    /**
     * multipart/form-data; boundary=----WebKitFormBoundaryrxAQf0xiHVEugteF
     * System.out.println(request.getContentType());
     */
    @PostMapping("/addFile")
    @ResponseBody
    public ResultDTO<String> addFile(@RequestParam("files")
                 MultipartFile[] multipartFiles, FileDTO fileDTO) {
        try {
            // 必填校验
            String originalFileNames = fileDTO.getOriginalFileNames();
            if (!StringUtils.hasText(originalFileNames)) {
                return ResultDTO.buildFailure("原始文件名不能为空,多个用,分隔");
            }
            String sizes = fileDTO.getSizes();
            if (!StringUtils.hasText(sizes)) {
                return ResultDTO.buildFailure("文件大小不能为空,多个用,分隔");
            }
            // 两者以及multipartFiles的顺序一致
            String[] originalFileNameArr = originalFileNames.split(",");
            String[] sizeArr = sizes.split(",");
            List<FileDO> tmpFileDOS = new ArrayList<>();
            for (int i = 0; i < originalFileNameArr.length; i++) {
                FileDO fileDO = new FileDO();
                fileDO.setOriginalFileName(originalFileNameArr[i]);
                fileDO.setSize(Long.valueOf(sizeArr[i]));
                tmpFileDOS.add(fileDO);
            }
            List<FileDO> finalList = getFinalList(multipartFiles,tmpFileDOS);
            // 记录失败的
            List<String> failureMsgList = new ArrayList<>();
            for (FileDO fileDO : finalList) {
                ResultDTO<String> addResult = fileManager.add(fileDO);
                if (!addResult.getSuccess()) {
                    // 文件名+错误信息
                    failureMsgList.add("["+fileDO.getOriginalFileName()+"]"+addResult.getErrMsg());
                }
            }
            if (failureMsgList.isEmpty()) {
                return ResultDTO.buildSuccess("添加文件成功");
            }else {
                String str = String.join(",", failureMsgList);
                return ResultDTO.buildFailure("失败清单:"+str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultDTO.buildFailure("添加文件出错");
    }

    private List<FileDO> getFinalList(MultipartFile[] multipartFiles, List<FileDO> tmpFileDOS) {
        List<FileDO> finalList = new ArrayList<>();
        for (int i = 0; i < multipartFiles.length; i++) {
            MultipartFile multipartFile = multipartFiles[i];
            if (multipartFile.isEmpty()) {
                log.warn("未上传文件或上传的文件内容为空");
                continue;
            }
            String originalFilename = multipartFile.getOriginalFilename();
            if (!StringUtils.hasText(originalFilename)) {
                log.warn("上传的文件名为空");
                continue;
            }
            String relativePath = fileService.upload(multipartFile);
            if (!StringUtils.hasText(relativePath)) {
                log.warn("文件存储路径不能为空");
                continue;
            }
            FileDO fileDO = tmpFileDOS.get(i);
            fileDO.setStoreRelativePath(relativePath);
            finalList.add(fileDO);
        }
        return finalList;
    }

    @PostMapping("/deleteByIds")
    @ResponseBody
    public ResultDTO<String> deleteByIds(@RequestBody FileDTO dto) {
        List<String> pathsToDelete = dto.getPathsToDelete();
        if (pathsToDelete == null || pathsToDelete.isEmpty()) {
            return ResultDTO.buildFailure("pathsToDelete必传");
        }
        try {
            return fileManager.deleteByPaths(pathsToDelete);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultDTO.buildFailure("删除文件出错");
    }
}
