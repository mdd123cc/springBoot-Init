package com.mdd.bootInit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.bootInit.model.dto.user.UserQueryRequest;
import com.mdd.bootInit.model.dto.user.UserUpdateRequest;
import com.mdd.bootInit.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mdd.bootInit.model.vo.LoginUserVo;
import com.mdd.bootInit.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 21958
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2023-06-25 01:14:18
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注册
     * @param request
     * @return
     */
    Boolean userLogout(HttpServletRequest request);

    /**
     * 更新用户
     * @param user
     * @return
     */
    boolean updateUserInfo(User user);

    /**
     * 添加用户
     * @param user
     * @return
     */
    User addUser(User user);

    /**
     * 删除用户
     * @param id
     * @return
     */
    boolean deleteUser(Long id);

    /**
     * 获取用户信息(脱敏)
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 用户列表脱敏
     * @param page
     * @return
     */
    List<UserVO> getUserVoList(List<User> page);

    /**
     * 获取用户分页（管理）
     * @param userQueryRequest
     * @return
     */
    Page<User> userListByPage(UserQueryRequest userQueryRequest);

    /**
     * 获取查询条件构造器
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 用户信息列表分页
     * @param userQueryRequest
     * @return
     */
    Page<UserVO> listUserVoByPage(UserQueryRequest userQueryRequest);


    /**
     * 获取当前登录用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);
}
