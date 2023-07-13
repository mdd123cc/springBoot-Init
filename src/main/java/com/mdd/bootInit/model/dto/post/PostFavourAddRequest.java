package com.mdd.bootInit.model.dto.post;

import lombok.Data;

import java.io.Serializable;

/**
 * 收藏请求体
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@Data
public class PostFavourAddRequest implements Serializable {

    /**
     * 帖子 id
     */
    private Long postId;

    private static final long serialVersionUID = 1L;
}
