package com.mdd.bootInit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mdd.bootInit.annotation.AuthCheck;
import com.mdd.bootInit.common.BaseResponse;
import com.mdd.bootInit.common.DeleteRequest;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.common.ResultUtils;
import com.mdd.bootInit.constant.UserConstant;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.exception.ThrowUtils;
import com.mdd.bootInit.model.dto.user.*;
import com.mdd.bootInit.model.entity.User;
import com.mdd.bootInit.model.vo.LoginUserVo;
import com.mdd.bootInit.model.vo.UserVO;
import com.mdd.bootInit.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 *
 * @author Mdd
 * @Github <a href="https://github.com/mdd123cc"/>
 * @Gitee <a href="https://gitee.com/mdd115192"/>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // region 登录相关

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        //参数校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long res = userService.userRegister(userAccount,userPassword,checkPassword);
        return ResultUtils.success(res);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        //参数校验
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //Service
        LoginUserVo loginUserVo = userService.userLogin(userAccount,userPassword,request);
        return ResultUtils.success(loginUserVo);
    }


    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request){
        //参数校验
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean res = userService.userLogout(request);
        return ResultUtils.success(res);
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     * @param userAddRequest
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest){
        //校验
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest,user);
        User res = userService.addUser(user);
        return ResultUtils.success(res.getId());
    }


    /**
     * 删除用户
     * @param userDeleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest userDeleteRequest){
        if (userDeleteRequest == null||userDeleteRequest.getId()<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = userService.deleteUser(userDeleteRequest.getId());
        return ResultUtils.success(res);
    }

    /**
     * 根据id获取用户信息(脱敏)
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 用户列表分页（管理员）
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest){
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<User> userPage = userService.userListByPage(userQueryRequest);
        return ResultUtils.success(userPage);
    }

    /**
     * 用户信息列表分页
     * @param userQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Page<UserVO> userVOPage = userService.listUserVoByPage(userQueryRequest);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 更新用户
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInfo(@RequestBody UserUpdateRequest userUpdateRequest){
        //参数校验
        if (userUpdateRequest == null||userUpdateRequest.getId()==null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //数据拷贝
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest,user);

        boolean res = userService.updateUserInfo(user);
        //校验是否更新成功
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据id查询用户（仅管理员）
     * @param id
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<User>getUserById(Long id){
        //校验
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //查询
        User user = userService.getById(id);
        //校验结果
        ThrowUtils.throwIf(user==null,ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    // endregion

    /**
     * 更改个人信息
     * @param userUpdateMyRequest
     * @return
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyInfo(@RequestBody UserUpdateMyRequest userUpdateMyRequest
    ,HttpServletRequest request){
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户信息
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest,user);
        user.setId(loginUser.getId());
        boolean res = userService.updateById(user);
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}
