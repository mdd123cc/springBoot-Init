package com.mdd.bootInit.exception;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.mdd.bootInit.common.BaseResponse;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理自定义异常
     * @param exception
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException exception){
        //日志打印异常信息
        log.error("BusinessException",exception);
        //向前端相应异常信息
        return ResultUtils.error(exception.getCode(),exception.getMessage());
    }

    /**
     * 处理运行异常
     * @param runtimeException
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException runtimeException){
        log.error("RuntimeException",runtimeException);
        //运行异常异常统一响应内部错误给前端
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,runtimeException.getMessage());
    }
}
