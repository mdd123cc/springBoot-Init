package com.mdd.bootInit.service;

import com.mdd.bootInit.model.dto.post.PostThumbAddRequest;
import com.mdd.bootInit.model.entity.PostThumb;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author 21958
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Service
* @createDate 2023-07-13 19:45:39
*/
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 帖子点赞/取消点赞
     * @param postThumbAddRequest
     * @param request
     * @return
     */
    int doThumb(PostThumbAddRequest postThumbAddRequest, HttpServletRequest request);
}
