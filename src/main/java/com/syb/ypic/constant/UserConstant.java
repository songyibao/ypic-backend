package com.syb.ypic.constant;

public interface UserConstant {
    // region 用户角色
    
    String ADMIN = "admin";
    String USER = "user";

    // endregion


    /**
     * 用户登陆态键
     */
    String USER_LOGIN_STATE_KEY = "user_login_state_key";

    /**
     * 用户密码加密盐值
     */
    String USER_PASSWORD_SALT = "ypic-666";

    /**
     * 默认的用户密码
     */
    String DEFAULT_USER_PASSWORD = "123456789";
}
