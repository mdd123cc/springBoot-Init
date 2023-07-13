package com.mdd.bootInit.service;

import com.mdd.bootInit.model.dto.post.PostFavourAddRequest;
import com.mdd.bootInit.model.entity.PostFavour;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 21958
* @description 针对表【post_favour(帖子收藏)】的数据库操作Service
* @createDate 2023-07-13 19:45:43
*/
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 收藏/取消收藏帖子
     * @param postThumbAddRequest
     * @param request
     * @return
     */
    int doFavour(PostFavourAddRequest postThumbAddRequest, HttpServletRequest request);

    int doFavour(long  postId, long  userId);
    }
