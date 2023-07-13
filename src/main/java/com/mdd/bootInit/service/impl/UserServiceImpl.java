package com.mdd.bootInit.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mdd.bootInit.common.ErrorCode;
import com.mdd.bootInit.exception.BusinessException;
import com.mdd.bootInit.exception.ThrowUtils;
import com.mdd.bootInit.model.dto.user.UserQueryRequest;
import com.mdd.bootInit.model.entity.User;
import com.mdd.bootInit.model.vo.LoginUserVo;
import com.mdd.bootInit.model.vo.UserVO;
import com.mdd.bootInit.service.UserService;
import com.mdd.bootInit.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

import static com.mdd.bootInit.constant.UserConstant.USER_LOGIN_STATIC;

/**
* @author 21958
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2023-06-25 01:14:18
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{
    private static final String SALT = "mdd";

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        //业务层次的参数格式校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        //账户名不能小于4
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        //加互斥锁保证用户注册幂等性
        synchronized (userAccount.intern()) {
            //查看用户是否已经存在
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getUserAccount, userAccount);
            long count = count(queryWrapper);
            if (count != 0) {
                //说明用户已经存在
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
            }
            //对用户密码加盐加密
            String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
            //插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            //添加用户信息
            boolean res = save(user);
            if (!res) throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败");
            return user.getId();
        }
    }

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    @Override
    public LoginUserVo userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        //加密
        String encryptPassword = DigestUtils.md5DigestAsHex((userPassword + SALT).getBytes());
        //检查用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount)
                .eq(User::getUserPassword,encryptPassword);
        User user = getOne(queryWrapper);
        if (user == null) {
            //用户密码错误或者不存在
            log.error("user login failed");
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在或密码错误!");
        }
        //记录用户的登录态到会话中
        request.getSession().setAttribute(USER_LOGIN_STATIC,user);
        //用户脱敏
        LoginUserVo loginUserVo = getLoginUserVo(user);
        return loginUserVo;
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @Override
    public Boolean userLogout(HttpServletRequest request) {
        //校验
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"request对象为空");
        }
        //校验是否登录
        if (request.getSession().getAttribute(USER_LOGIN_STATIC) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR,"未登录");
        }
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATIC);
        return true;
    }

    /**
     * 更新用户
     * @param user
     * @return
     */
    @Override
    public boolean updateUserInfo(User user) {
        //校验
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return updateById(user);
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public User addUser(User user) {
        //校验
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //管理员创建用户都有默认密码123456
        String defaultPwd = "123456";
        String encryptPassword = DigestUtils.md5DigestAsHex((defaultPwd + SALT).getBytes());
        user.setUserPassword(encryptPassword);
        boolean res = save(user);
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return user;
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Override
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean res = removeById(id);
        ThrowUtils.throwIf(!res,ErrorCode.OPERATION_ERROR);
        return res;
    }

    /**
     * 获取用户信息（脱敏）
     * @param user
     * @return
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user,userVO);
        return userVO;
    }

    /**
     * 用户列表脱敏
     * @param userList
     * @return
     */
    @Override
    public List<UserVO> getUserVoList(List<User> userList){
        if (userList == null) {
            return null;
        }
        //stream流处理列表
        return userList.stream().map(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user,userVO);
            return userVO;
        }).collect(Collectors.toList());
    }

    /**
     * 获取用户分页（管理）
     * @param userQueryRequest
     * @return
     */
    @Override
    public Page<User> userListByPage(UserQueryRequest userQueryRequest) {
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        //定义page对象
        Page<User> userPage = new Page<>(current,pageSize);
        QueryWrapper<User> queryWrapper = getQueryWrapper(userQueryRequest);
        return page(userPage, queryWrapper);
    }

    /**
     * 用户脱敏，得到用户信息脱敏对象
     * @param user
     * @return
     */
    private LoginUserVo getLoginUserVo(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        BeanUtils.copyProperties(user,loginUserVo);
        return loginUserVo;
    }

    /**
     * 用户信息列表分页
     * @param userQueryRequest
     * @return
     */
    @Override
    public Page<UserVO> listUserVoByPage(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        //限制爬虫
        ThrowUtils.throwIf(pageSize>30,ErrorCode.PARAMS_ERROR);
        //构建page对象
        Page<User> userPage = new Page<>(current, pageSize);
        //构建条件构造器
        QueryWrapper<User> queryWrapper = getQueryWrapper(userQueryRequest);
        //分页查询
        Page<User> page = page(userPage, queryWrapper);
        //转化
        Page<UserVO> userVOPage = new Page<>();
        BeanUtils.copyProperties(page,userVOPage,"records");
        userVOPage.setRecords(getUserVoList(page.getRecords()));
        return userVOPage;
    }

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        //先判断是否登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATIC);
        User loginUser = (User) userObj;
        if (loginUser == null || loginUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        User user = getById(loginUser.getId());
        return user;
    }

    /**
     * 获取查询条件构造器
     * @param userQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String unionId = userQueryRequest.getUnionId();
        // todo 缺失
        //String mpOpenId = userQueryRequest.getMpOpenId();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        //String sortOrder = userQueryRequest.getSortOrder();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
        //queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        //queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                //sortField);
        return queryWrapper;
    }

}




