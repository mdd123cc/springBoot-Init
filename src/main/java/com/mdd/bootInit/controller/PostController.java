package com.mdd.bootInit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.bootInit.annotation.AuthCheck;
import com.mdd.bootInit.common.BaseResponse;
import com.mdd.bootInit.common.DeleteRequest;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.common.ResultUtils;
import com.mdd.bootInit.constant.UserConstant;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.model.dto.post.PostAddRequest;
import com.mdd.bootInit.model.dto.post.PostEditRequest;
import com.mdd.bootInit.model.dto.post.PostQueryRequest;
import com.mdd.bootInit.model.dto.post.PostUpdateRequest;
import com.mdd.bootInit.model.vo.PostVo;
import com.mdd.bootInit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子接口
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@RequestMapping("/post")
@RestController
@Slf4j
public class PostController {

    @Resource
    private PostService postService;

    // region 增删改查

    /**
     * 创建帖子
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        //校验
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = postService.addPost(postAddRequest,request);
        return ResultUtils.success(id);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest,HttpServletRequest request) {
        if (deleteRequest == null||deleteRequest.getId()<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = postService.deletePost(deleteRequest,request);
        return ResultUtils.success(res);
    }


    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = postService.updatePost(postUpdateRequest);
        return ResultUtils.success(res);
    }

    /**
     * 根据 id 查询帖子信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<PostVo> getPostVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PostVo postVo = postService.getPostVoById(id,request);
        return ResultUtils.success(postVo);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PostVo>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                       HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<PostVo> postVoPage = postService.listPostVoByPage(postQueryRequest,request);
        return ResultUtils.success(postVoPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param postQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<PostVo>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                         HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<PostVo> postVoPage = postService.listMyPostByPage(postQueryRequest,request);
        return ResultUtils.success(postVoPage);
    }

    // endregion

    /**
     * 编辑帖子（用户）
     *
     * @param postEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editPost(@RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = postService.editPost(postEditRequest,request);
        return ResultUtils.success(res);
    }
}
