package com.syb.ypic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.syb.ypic.common.ResultUtils;
import com.syb.ypic.constant.OrderByConstant;
import com.syb.ypic.constant.UserConstant;
import com.syb.ypic.exception.ErrorCode;
import com.syb.ypic.exception.ThrowUtils;
import com.syb.ypic.model.dto.UserQueryRequest;
import com.syb.ypic.model.entity.User;
import com.syb.ypic.model.dto.UserAddRequest;
import com.syb.ypic.model.enums.UserRoleEnum;
import com.syb.ypic.model.vo.LoginUserVO;
import com.syb.ypic.model.vo.UserVO;
import com.syb.ypic.service.UserService;
import com.syb.ypic.mapper.UserMapper;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author songyibao
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2025-02-14 17:01:41
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 参数校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword, checkPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
        ThrowUtils.throwIf(userAccount.length() < 8, ErrorCode.PARAMS_ERROR, "账号长度不能小于8位");
        ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码长度不能小于8位");
        ThrowUtils.throwIf(!userPassword.equals(checkPassword), ErrorCode.PARAMS_ERROR, "两次密码不一致");

        // 2. 判断账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.baseMapper.selectCount(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "账号已存在");

        // 3. 密码一定要加密
        userPassword = getEncryptPassword(userPassword);

        // 4. 保存用户信息到数据库
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        user.setUserName(userAccount);
        user.setUserRole(UserRoleEnum.USER.getValue());
        boolean saveResult = this.save(user);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "注册失败");

        return user.getId();
    }

    @Override
    public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 参数校验
        ThrowUtils.throwIf(StrUtil.hasBlank(userAccount, userPassword), ErrorCode.PARAMS_ERROR, "参数不能为空");
        // 2. 查询用户信息,判断密码是否正确
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", getEncryptPassword(userPassword));
        User user = this.baseMapper.selectOne(queryWrapper);
        ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "账号或密码错误");
        // 3. 保存session,返回用户信息
        LoginUserVO loginUserVO = toLoginUserVO(user);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE_KEY, loginUserVO);
        return loginUserVO;
    }

    @Override
    public String getEncryptPassword(String password) {
        final String salt = UserConstant.USER_PASSWORD_SALT;
        return DigestUtils.md5DigestAsHex((password + salt).getBytes());
    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE_KEY);
        LoginUserVO currentLoginUser = (LoginUserVO) attribute;
        ThrowUtils.throwIf(currentLoginUser == null || currentLoginUser.getId() == null, ErrorCode.NOT_LOGIN_ERROR);
        User user = this.getById(currentLoginUser.getId());
        // 可能这个用户已经被删除了，等等
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_LOGIN_ERROR);
        return user;
    }

    @Override
    public boolean userLogout(HttpServletRequest request) {
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE_KEY);
        ThrowUtils.throwIf(attribute == null, ErrorCode.NOT_LOGIN_ERROR);
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE_KEY);
        return true;
    }

    @Override
    public long addUser(UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        user.setUserPassword(UserConstant.DEFAULT_USER_PASSWORD);
        if (StrUtil.isBlank(user.getUserRole())) {
            user.setUserRole(UserRoleEnum.USER.getValue());
        }
        boolean result = this.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "新增用户失败");
        return user.getId();
    }

    @Override
    public LoginUserVO toLoginUserVO(User user) {
        if (user == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtil.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public UserVO toUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public List<UserVO> toUserVOList(List<User> userList) {
        if (CollUtil.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::toUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        Long id = userQueryRequest.getId();
        String userName = userQueryRequest.getUserName();
        String userAccount = userQueryRequest.getUserAccount();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();

        int current = userQueryRequest.getCurrent();
        int pageSize = userQueryRequest.getPageSize();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.eq(StrUtil.isNotBlank(userRole), "userRole", userRole);
        queryWrapper.like(StrUtil.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StrUtil.isNotBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(StrUtil.isNotBlank(userProfile), "userProfile", userProfile);
        queryWrapper.orderBy(StrUtil.isNotBlank(sortField), sortOrder.equals(OrderByConstant.ASC), sortField);

        return queryWrapper;
    }

    @Override
    public Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "查询参数为空");
        QueryWrapper<User> queryWrapper = getQueryWrapper(userQueryRequest);
        Page<User> page = new Page<>(userQueryRequest.getCurrent(), userQueryRequest.getPageSize());
        Page<User> userPage = this.page(page, queryWrapper);
        List<UserVO> userVOList = toUserVOList(userPage.getRecords());
        Page<UserVO> userVOPage = new Page<>();
        BeanUtil.copyProperties(userPage, userVOPage);
        userVOPage.setRecords(userVOList);
        return userVOPage;

    }
}




