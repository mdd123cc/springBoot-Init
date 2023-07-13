package com.mdd.bootInit.controller;

import com.mdd.bootInit.common.BaseResponse;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.common.ResultUtils;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.model.dto.post.PostFavourAddRequest;
import com.mdd.bootInit.model.dto.post.PostThumbAddRequest;
import com.mdd.bootInit.service.PostFavourService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@RestController
@Slf4j
@RequestMapping("/post")
public class PostFavourController {


    @Resource
    private PostFavourService postFavourService;

    /**
     * 帖子点赞/取消点赞
     * @param postFavourAddRequest
     * @param request
     * @return
     */
    @PostMapping("/post_favour")
    public BaseResponse<Integer> doFavour(@RequestBody PostFavourAddRequest postFavourAddRequest,
                                         HttpServletRequest request) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int thumbNum = postFavourService.doFavour(postFavourAddRequest,request);
        return ResultUtils.success(thumbNum);
    }
}
