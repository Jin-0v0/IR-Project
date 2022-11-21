package com.roadjava.doc.search.bean.res;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhaodaowen
 * @see <a href="http://www.roadjava.com">乐之者java</a>
 */
@Data
public class ResultDTO<T> {
    private String errCode;
    private String errMsg;
    /**
     * 本次返回结果是否成功
     */
    private Boolean success = Boolean.TRUE;
    private T data;
    private Long total;
    private Map<String,Object> attributes = new HashMap<>();
    private ResultDTO(){
    }

    public void addAttr(String key,Object value){
        this.attributes.put(key,value);
    }

    public static <T> ResultDTO<T> buildSuccess(T t, Long total){
        ResultDTO<T> resultDTO = buildSuccess(t);
        resultDTO.setTotal(total);
        return resultDTO;
    }

    public static <T> ResultDTO<T> buildEmptySuccess(){
        return new ResultDTO<>();
    }

    public static <T> ResultDTO<T> buildSuccess(T t){
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setData(t);
        return resultDTO;
    }
    public static <T> ResultDTO<T> buildFailure(String code, String errMsg) {
        ResultDTO<T> resultDTO = new ResultDTO<>();
        resultDTO.setErrCode(code);
        resultDTO.setErrMsg(errMsg);
        resultDTO.setSuccess(false);
        return resultDTO;
    }

    public static <T> ResultDTO<T> buildFailure(String errMsg) {
        return buildFailure(null,errMsg);
    }

}
