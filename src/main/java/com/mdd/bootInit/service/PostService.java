package com.mdd.bootInit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.bootInit.common.DeleteRequest;
import com.mdd.bootInit.model.dto.post.PostAddRequest;
import com.mdd.bootInit.model.dto.post.PostEditRequest;
import com.mdd.bootInit.model.dto.post.PostQueryRequest;
import com.mdd.bootInit.model.dto.post.PostUpdateRequest;
import com.mdd.bootInit.model.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mdd.bootInit.model.vo.PostVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author 21958
* @description 针对表【post(帖子)】的数据库操作Service
* @createDate 2023-07-12 20:19:49
*/
public interface PostService extends IService<Post> {

    /**
     * 校验
     *
     * @param post
     * @param add
     */
    void validPost(Post post, boolean add);

    /**
     * 创建帖子
     * @param postAddRequest
     * @return
     */
    Long addPost(PostAddRequest postAddRequest, HttpServletRequest request);

    /**
     * 删除帖子
     * @param deleteRequest
     * @return
     */
    boolean deletePost(DeleteRequest deleteRequest,HttpServletRequest request);

    /**
     * 修改帖子（管理员）
     * @param postUpdateRequest
     * @return
     */
    boolean updatePost(PostUpdateRequest postUpdateRequest);

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    PostVo getPostVoById(long id,HttpServletRequest request);

    /**
     * 获取帖子封装
     * @param post
     * @return
     */
    PostVo getPostVo(Post post,HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param postPage
     * @param request
     * @return
     */
    Page<PostVo> getPostVOPage(Page<Post> postPage, HttpServletRequest request);
    /**
     * 帖子列表分页
     * @param postQueryRequest
     * @return
     */
    Page<PostVo> listPostVoByPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

    /**
     * 根据查询条件获取条件构造器
     *
     * @param postQueryRequest
     * @return
     */
    QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 当前用户的帖子分页
     * @param postQueryRequest
     * @param request
     * @return
     */
    Page<PostVo> listMyPostByPage(PostQueryRequest postQueryRequest, HttpServletRequest request);

    /**
     * 用户编辑帖子
     * @param postEditRequest
     * @param request
     * @return
     */
    boolean editPost(PostEditRequest postEditRequest, HttpServletRequest request);
}
