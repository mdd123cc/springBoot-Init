package com.mdd.bootInit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.exception.ThrowUtils;
import com.mdd.bootInit.mapper.PostThumbMapper;
import com.mdd.bootInit.model.dto.post.PostThumbAddRequest;
import com.mdd.bootInit.model.entity.Post;
import com.mdd.bootInit.model.entity.PostThumb;
import com.mdd.bootInit.model.entity.User;
import com.mdd.bootInit.service.PostService;
import com.mdd.bootInit.service.PostThumbService;
import com.mdd.bootInit.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author 21958
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Service实现
* @createDate 2023-07-13 19:45:39
*/
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
    implements PostThumbService{

    @Resource
    private UserService userService;

    @Resource
    private PostService postService;

    /**
     * 帖子点赞/取消点赞
     * @param postThumbAddRequest
     * @param request
     * @return
     */
    @Override
    public int doThumb(PostThumbAddRequest postThumbAddRequest, HttpServletRequest request) {
        //校验
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //帖子是否存在
        Long postId = postThumbAddRequest.getPostId();
        Post post = postService.getById(postId);
        ThrowUtils.throwIf(post==null,ErrorCode.NOT_FOUND_ERROR);
        //加锁，防止出现多点多取消的情况,保证幂等性
        PostThumbServiceImpl postThumbService = (PostThumbServiceImpl) AopContext.currentProxy();
        synchronized (loginUser.getId()+"".intern()){
            return postThumbService.doThumb(postId,loginUser.getId());
        }
    }

    /**
     * 点赞/取消点赞
     * @param postId
     * @param userId
     * @return
     */
    @Transactional
    int doThumb(long postId, long userId){
        //判断是否点过赞
        LambdaQueryWrapper<PostThumb> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostThumb::getPostId,postId).eq(PostThumb::getUserId,userId);
        PostThumb postThumb = getOne(queryWrapper);
        //判断当前用户是否点过赞
        if (postThumb == null) {
            //没有点赞
            PostThumb postThumb1 = new PostThumb();
            postThumb1.setPostId(postId);
            postThumb1.setUserId(userId);
            boolean save = save(postThumb1);
            ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR);
            //更新帖子的点赞数
            boolean res = postService.update().eq("id", postId).setSql("thumbNum=thumbNum+1").update();
            return res?1:0;
        }else {
            //已点赞
            //删除点赞记录
            boolean remove = removeById(postThumb);
            ThrowUtils.throwIf(!remove,ErrorCode.OPERATION_ERROR);
            //点赞数-1
            boolean res = postService.update().eq("id", postId)
                    .gt("thumbNum", 0)
                    .eq("id", postId).setSql("thumbNum=thumbNum-1").update();
            return res?-11:0;
        }
    }
}




