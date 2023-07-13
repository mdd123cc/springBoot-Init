package com.mdd.bootInit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体类
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
