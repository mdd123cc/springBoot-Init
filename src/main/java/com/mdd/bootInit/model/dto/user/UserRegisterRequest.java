package com.mdd.bootInit.model.dto.user;

import lombok.Data;

/**
 * 用户注册请求体类
 * 接受用户注册请求体的请求参数
 *
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@Data
public class UserRegisterRequest {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;

}
