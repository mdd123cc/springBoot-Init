package com.mdd.bootInit.common;

/**
 * 自定义的错误码
 *
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
public enum ErrorCode {
    SUCCESS(0,"OK"),
    PARAMS_ERROR(40000,"请求参数错误"),
    NOT_LOGIN_ERROR(40100,"未登录"),
    NO_AUTH_ERROR(40200,"没有权限"),
    NOT_FOUND_ERROR(40300,"请求参数不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
