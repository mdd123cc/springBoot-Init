package com.mdd.bootInit.mapper;

import com.mdd.bootInit.model.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 21958
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2023-06-25 01:14:18
* @Entity com.mdd.bootInit.model.entity.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




