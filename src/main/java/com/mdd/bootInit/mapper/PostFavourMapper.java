package com.mdd.bootInit.mapper;

import com.mdd.bootInit.model.entity.PostFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 21958
* @description 针对表【post_favour(帖子收藏)】的数据库操作Mapper
* @createDate 2023-07-13 19:45:43
* @Entity com.mdd.bootInit.model.entity.PostFavour
*/
@Mapper
public interface PostFavourMapper extends BaseMapper<PostFavour> {

}




