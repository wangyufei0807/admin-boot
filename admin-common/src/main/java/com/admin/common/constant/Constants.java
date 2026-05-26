package com.admin.common.constant;

/**
 * 通用常量
 */
public class Constants {
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * 下划线
     */
    public static final String UNDERLINE = "_";

    /**
     * 点
     */
    public static final String DOT = ".";

    /**
     * 星号
     */
    public static final String ASTERISK = "*";

    /**
     * 逗号
     */
    public static final String COMMA = ",";

    /**
     * 成功标记
     */
    public static final int SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final int FAIL = 500;

    /**
     * 登录成功
     */
    public static final int LOGIN_SUCCESS = 200;

    /**
     * 登录失败
     */
    public static final int LOGIN_FAIL = 401;

    /**
     * JWT Token 前缀
     */
    public static final String JWT_PREFIX = "Bearer ";

    /**
     * 登录用户 Redis Key 前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 接口限流 Redis Key 前缀
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";
}
