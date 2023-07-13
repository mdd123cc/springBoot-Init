package com.mdd.bootInit.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用的返回结果类
 *
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@Data
public class BaseResponse<T> implements Serializable {

    //状态码
    private int code;

    //响应的数据
    private T data;

    //响应的信息
    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(T data){
        this(200,data,"");
    }

    public BaseResponse(int code ,T data){
        this(code,data,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage());
    }
}
