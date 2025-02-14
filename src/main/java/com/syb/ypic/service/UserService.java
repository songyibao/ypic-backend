package com.syb.ypic.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syb.ypic.model.dto.UserQueryRequest;
import com.syb.ypic.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.syb.ypic.model.dto.UserAddRequest;
import com.syb.ypic.model.vo.LoginUserVO;
import com.syb.ypic.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author songyibao
 * @description 针对表【user(用户)】的数据库操作Service
 * @createDate 2025-02-14 17:01:41
 */
public interface UserService extends IService<User> {

    /**
     * 用户登录
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 确认密码
     * @return 用户信息
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      请求
     * @return 用户信息
     */

    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    String getEncryptPassword(String password);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 用户注销当前登录态
     */
    boolean userLogout(HttpServletRequest request);

    /**
     * 新增用户
     */
    long addUser(UserAddRequest userAddRequest);


    /**
     * 获取脱敏的登录用户信息
     *
     * @param user 用户
     * @return 脱敏登录用户信息
     */
    LoginUserVO toLoginUserVO(User user);

    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户
     * @return 脱敏用户信息
     */
    UserVO toUserVO(User user);

    List<UserVO> toUserVOList(List<User> userList);

    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 分页查询用户信息（管理员）
     *
     * @param userQueryRequest
     * @return
     */
    Page<UserVO> listUserVOByPage(UserQueryRequest userQueryRequest);
}
