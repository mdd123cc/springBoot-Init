package com.mdd.bootInit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.exception.ThrowUtils;
import com.mdd.bootInit.mapper.PostFavourMapper;
import com.mdd.bootInit.model.dto.post.PostFavourAddRequest;
import com.mdd.bootInit.model.entity.Post;
import com.mdd.bootInit.model.entity.PostFavour;
import com.mdd.bootInit.model.entity.PostThumb;
import com.mdd.bootInit.model.entity.User;
import com.mdd.bootInit.service.PostFavourService;
import com.mdd.bootInit.service.PostService;
import com.mdd.bootInit.service.UserService;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author 21958
* @description 针对表【post_favour(帖子收藏)】的数据库操作Service实现
* @createDate 2023-07-13 19:45:43
*/
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
    implements PostFavourService{

    @Resource
    private PostService postService;

    @Resource
    private UserService userService;

    @Override
    public int doFavour(PostFavourAddRequest postFavourAddRequest, HttpServletRequest request) {
        //校验
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        //帖子是否存在
        Long postId = postFavourAddRequest.getPostId();
        Post post = postService.getById(postId);
        ThrowUtils.throwIf(post==null,ErrorCode.NOT_FOUND_ERROR);
        //加锁，防止出现多点多取消的情况,保证幂等性
        PostFavourService postFavourService = (PostFavourService) AopContext.currentProxy();
        synchronized (loginUser.getId()+"".intern()){
            return postFavourService.doFavour(postId,loginUser.getId());
        }
    }

    /**
     * 收藏/取消收藏
     * @param postId
     * @param userId
     * @return
     */
    @Override
    public int doFavour(long  postId, long  userId) {
        //判断是否点过赞
        LambdaQueryWrapper<PostFavour> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostFavour::getPostId,postId).eq(PostFavour::getUserId,userId);
        PostFavour postFavour = getOne(queryWrapper);
        //判断当前用户是否点过赞
        if (postFavour == null) {
            //没有点赞
            PostFavour postFavour1 = new PostFavour();
            postFavour1.setPostId(postId);
            postFavour1.setUserId(userId);
            boolean save = save(postFavour1);
            ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR);
            //更新帖子的点赞数
            boolean res =  postService.update()
                    .eq("id", postId)
                    .setSql("favourNum = favourNum + 1")
                    .update();
            return res?1:0;
        }else {
            //已点赞
            //删除点赞记录
            boolean remove = removeById(postFavour);
            ThrowUtils.throwIf(!remove,ErrorCode.OPERATION_ERROR);
            //点赞数-1
            boolean res = postService.update().eq("id", postId)
                    .gt("favourNum", 0)
                    .eq("id", postId).setSql("favourNum=favourNum-1").update();
            return res?-1:0;
        }
    }
}




