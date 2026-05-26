package com.admin.common.context;

import com.admin.common.security.LoginUser;

/**
 * 用户上下文工具类
 */
public class UserContext {

    private static final ThreadLocal<LoginUser> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前登录用户
     */
    public static void setUser(LoginUser loginUser) {
        USER_THREAD_LOCAL.set(loginUser);
    }

    /**
     * 获取当前登录用户
     */
    public static LoginUser getUser() {
        return USER_THREAD_LOCAL.get();
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getUser();
        return loginUser != null ? loginUser.getUserId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        LoginUser loginUser = getUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }

    /**
     * 移除当前登录用户
     */
    public static void remove() {
        USER_THREAD_LOCAL.remove();
    }
}
