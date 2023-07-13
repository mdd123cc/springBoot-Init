package com.mdd.bootInit.mapper;

import com.mdd.bootInit.model.entity.PostThumb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 21958
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Mapper
* @createDate 2023-07-13 19:45:37
* @Entity com.mdd.bootInit.model.entity.PostThumb
*/
@Mapper
public interface PostThumbMapper extends BaseMapper<PostThumb> {

}




