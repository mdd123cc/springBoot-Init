package com.mdd.bootInit.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更改个人信息请求体
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 简介
     */
    private String userProfile;

    private static final long serialVersionUID = 1L;
}
