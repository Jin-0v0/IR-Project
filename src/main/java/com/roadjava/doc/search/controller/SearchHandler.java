package com.roadjava.doc.search.controller;

import com.roadjava.doc.search.bean.req.SearchRequest;
import com.roadjava.doc.search.bean.res.ResultDTO;
import com.roadjava.doc.search.bean.vo.FileVO;
import com.roadjava.doc.search.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchHandler {
    @Autowired
    private IndexService indexService;

    @PostMapping("/search") @ResponseBody
    public ResultDTO<List<FileVO>> search(@RequestBody @Valid SearchRequest request
            , BindingResult bindingResult){
        // 参数校验
        if (bindingResult.hasErrors()) {
            String errMsg = bindingResult.getAllErrors()
                    .stream()
                    .map(err -> err.getObjectName() + ":" + err.getDefaultMessage())
                    .collect(Collectors.joining(","));
            return ResultDTO.buildFailure(errMsg);
        }
        // 执行查询
        return indexService.search(request);
    }

    @GetMapping("/")
    public String toIndex(){
        return "frontend/index";
    }

    @GetMapping("/searchPage")
    public String searchPage(HttpServletRequest request){
        // 获取重定向RedirectAttributes中传递的参数
        String searchField = request.getParameter("searchField");
        String searchWord = request.getParameter("searchWord");
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setSearchField(searchField);
        searchRequest.setSearchWord(searchWord);
        request.setAttribute("searchRequest",searchRequest);
        return "frontend/search";
    }

    /**
     * 携带选择条件跳转到展示结果页面
     * @param request
     * @return
     */
    @PostMapping("/toSearch")
    public String toSearch(HttpServletRequest request, RedirectAttributes redirectAttributes ){
        try {
            String searchField = request.getParameter("searchField");
            String searchWord = request.getParameter("searchWord");
            // 相当于在地址栏加上参数:
            // http://localhost:8080/searchPage?searchField=cont&searchWord=%E9%9A%8F%E4%BE%BF
            redirectAttributes.addAttribute("searchField",searchField);
            redirectAttributes.addAttribute("searchWord",searchWord);
        }catch (Exception e){
            e.printStackTrace();
        }
        // 经过重定向解决刷新search.ftl页面弹出表单重复提交问题
        return "redirect:/searchPage";
    }

}
