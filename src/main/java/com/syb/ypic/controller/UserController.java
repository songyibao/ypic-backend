package com.syb.ypic.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syb.ypic.annotation.AuthCheck;
import com.syb.ypic.common.BaseResponse;
import com.syb.ypic.common.DeleteRequest;
import com.syb.ypic.common.ResultUtils;
import com.syb.ypic.constant.UserConstant;
import com.syb.ypic.exception.ErrorCode;
import com.syb.ypic.exception.ThrowUtils;
import com.syb.ypic.model.dto.UserLoginRequest;
import com.syb.ypic.model.dto.UserQueryRequest;
import com.syb.ypic.model.dto.UserRegisterRequest;
import com.syb.ypic.model.entity.User;
import com.syb.ypic.model.dto.UserAddRequest;
import com.syb.ypic.model.vo.LoginUserVO;
import com.syb.ypic.model.vo.UserVO;
import com.syb.ypic.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest request) {

        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        String userAccount = request.getUserAccount();
        String userPassword = request.getUserPassword();
        String checkPassword = request.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    /**
     * 用户退出
     *
     * @param request
     * @return 是否成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        Boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取登录用户
     *
     * @param request
     * @return 脱敏的登录用户信息
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        LoginUserVO result = userService.toLoginUserVO(loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 新增用户
     */
    @AuthCheck(mustRole = UserConstant.ADMIN)
    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        long result = userService.addUser(userAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户信息（管理员）
     */
    @AuthCheck(mustRole = UserConstant.ADMIN)
    @GetMapping("/get")
    public BaseResponse<User> getUserById(@RequestParam Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User result = userService.getById(id);
        ThrowUtils.throwIf(result == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 删除用户（管理员）
     */
    @AuthCheck(mustRole = UserConstant.ADMIN)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserById(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
        ThrowUtils.throwIf(deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR, "参数错误");
        Boolean result = userService.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "删除失败");
        return ResultUtils.success(result);
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        Page<UserVO> result = userService.listUserVOByPage(userQueryRequest);
        return ResultUtils.success(result);
    }


}
