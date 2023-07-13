package com.mdd.bootInit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.mdd.bootInit.common.DeleteRequest;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.constant.CommonConstant;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.exception.ThrowUtils;
import com.mdd.bootInit.model.dto.enums.UserRoleEnum;
import com.mdd.bootInit.model.dto.post.PostAddRequest;
import com.mdd.bootInit.model.dto.post.PostEditRequest;
import com.mdd.bootInit.model.dto.post.PostQueryRequest;
import com.mdd.bootInit.model.dto.post.PostUpdateRequest;
import com.mdd.bootInit.model.entity.Post;
import com.mdd.bootInit.model.entity.User;
import com.mdd.bootInit.model.vo.PostVo;
import com.mdd.bootInit.model.vo.UserVO;
import com.mdd.bootInit.service.PostService;
import com.mdd.bootInit.mapper.PostMapper;
import com.mdd.bootInit.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 21958
* @description 针对表【post(帖子)】的数据库操作Service实现
* @createDate 2023-07-12 20:19:49
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Resource
    private UserService userService;

    private Gson gson = new Gson();


    /**
     * 校验
     * @param post
     * @param add
     */
    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 创建帖子
     * @param postAddRequest
     * @return
     */
    @Override
    public Long addPost(PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest,post);
        //标签列表转json字符串
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            String tagsJson = gson.toJson(tags);
            post.setTags(tagsJson);
        }
        //参数校验
        validPost(post,true);
        //获取当前用户
        User loginUser = userService.getLoginUser(request);
        post.setUserId(loginUser.getId());
        post.setThumbNum(0);
        post.setFavourNum(0);
        //添加
        boolean res = save(post);
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return post.getId();
    }

    /**
     * 删除帖子
     * @param deleteRequest
     * @return
     */
    @Override
    public boolean deletePost(DeleteRequest deleteRequest,HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断帖子是否存在
        Post post = getById(deleteRequest.getId());
        ThrowUtils.throwIf(post==null,ErrorCode.NOT_FOUND_ERROR);
        //权限校验(只有本人和管理员可以删除)
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum roleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (roleEnum != UserRoleEnum.ADMIN && !loginUser.getId().equals(post.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean res = removeById(post.getId());
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return res;
    }

    /**
     * 修改帖子（管理员）
     * @param postUpdateRequest
     * @return
     */
    @Override
    public boolean updatePost(PostUpdateRequest postUpdateRequest) {
        //校验
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest,post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            String tagsJson = gson.toJson(tags);
            post.setTags(tagsJson);
        }
        //参数校验
        validPost(post,false);
        //帖子是否存在
        Post post1 = getById(post.getId());
        ThrowUtils.throwIf(post1==null,ErrorCode.NOT_FOUND_ERROR);
        boolean res = updateById(post);
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 根据id查询帖子
     * @param id
     * @return
     */
    @Override
    public PostVo getPostVoById(long id,HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = getById(id);
        ThrowUtils.throwIf(post==null,ErrorCode.PARAMS_ERROR);
        return getPostVo(post, request);
    }

    /**
     * 获得帖子封装
     * @param post
     * @return
     */
    @Override
    public PostVo getPostVo(Post post,HttpServletRequest request) {
        if (post == null) {
            return null;
        }
        PostVo postVo = PostVo.objToVo(post);
        //查询帖子创建人的详细信息
        Long id = postVo.getId();
        User user = userService.getById(id);
        UserVO userVO = userService.getUserVO(user);
        postVo.setUser(userVO);
        //判断是否登录
        User loginUser = userService.getLoginUser(request);
        // 已登录，获取用户点赞、收藏状态
        if (loginUser != null) {
            // todo 需要判断登录用户对该帖子是否点赞和收藏
        }
        return postVo;
    }

    @Override
    public Page<PostVo> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        Page<PostVo> postVoPage = new Page<>();
        BeanUtils.copyProperties(postPage,postVoPage,"records");
        List<PostVo> postVoList = postPage.getRecords().stream().map(post -> {
            PostVo postVo = PostVo.objToVo(post);
            //查询帖子创建人的详细信息
            Long id = post.getUserId();
            User user = userService.getById(id);
            UserVO userVO = userService.getUserVO(user);
            postVo.setUser(userVO);
            //判断是否登录
            User loginUser = userService.getLoginUser(request);
            // 已登录，获取用户点赞、收藏状态
            if (loginUser != null) {
                // todo 需要判断登录用户对该帖子是否点赞和收藏
            }
            return postVo;
        }).collect(Collectors.toList());
        postVoPage.setRecords(postVoList);
        return postVoPage;
    }

    /**
     * 帖子列表分页
     * @param postQueryRequest
     * @return
     */
    @Override
    public Page<PostVo> listPostVoByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //构建Page对象
        long current = postQueryRequest.getCurrent();
        long pageSize = postQueryRequest.getPageSize();
        //限制爬虫
        ThrowUtils.throwIf(pageSize>20,ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = new Page<>(current,pageSize);
        //根据查询条件构建条件构造器
        QueryWrapper<Post> queryWrapper = getQueryWrapper(postQueryRequest);
        page(postPage, queryWrapper);
        //得到帖子封装信息分页

        return getPostVOPage(postPage, request);
    }

    /**
     * 根据查询条件获取条件构造器
     * @param postQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        Long notId = postQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.like("title", searchText).or().like("content", searchText);
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollectionUtils.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(sortField!=null, sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<PostVo> listMyPostByPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postQueryRequest.getCurrent();
        long pageSize = postQueryRequest.getPageSize();
        //限制爬虫
        ThrowUtils.throwIf(pageSize>20,ErrorCode.PARAMS_ERROR);
        //构造page对象
        Page<Post> postPage = new Page<>(current, pageSize);
        //条件构造器
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
        QueryWrapper<Post> queryWrapper = getQueryWrapper(postQueryRequest);
        //分页查询
        Page<Post> page = page(postPage, queryWrapper);
        //获取帖子封装信息列表
        return getPostVOPage(page,request);
    }

    /**
     * 用户编辑帖子
     * @param postEditRequest
     * @param request
     * @return
     */
    @Override
    public boolean editPost(PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest,post);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            String json = gson.toJson(tags);
            post.setTags(json);
        }
        validPost(post,false);
        //判断是否存在
        Post byId = getById(post.getId());
        ThrowUtils.throwIf(byId==null,ErrorCode.NOT_FOUND_ERROR);
        //权限校验
        if (!byId.getUserId().equals(loginUser.getId())&&!UserRoleEnum.ADMIN.equals(UserRoleEnum.getEnumByValue(loginUser.getUserRole()))){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean res = updateById(post);
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return true;
    }
}




