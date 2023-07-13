package com.mdd.bootInit.controller;

import com.mdd.bootInit.common.BaseResponse;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.common.ResultUtils;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.model.dto.post.PostThumbAddRequest;
import com.mdd.bootInit.service.PostThumbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */

@RequestMapping("/post")
@RestController
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    /**
     * 帖子点赞/取消点赞
     * @param postThumbAddRequest
     * @param request
     * @return
     */
    @PostMapping("/post_thumb")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
                                         HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int thumbNum = postThumbService.doThumb(postThumbAddRequest,request);
        return ResultUtils.success(thumbNum);
    }

}
