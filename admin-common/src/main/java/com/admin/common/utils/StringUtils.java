package com.admin.common.utils;

import java.util.Collection;

/**
 * 字符串工具类
 */
public class StringUtils {

    private StringUtils() {
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为空（去除空格）
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空（去除空格）
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 字符串格式化
     */
    public static String format(String template, Object... params) {
        if (isEmpty(template) || params == null || params.length == 0) {
            return template;
        }
        return String.format(template, params);
    }

    /**
     * 判断集合是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 判断集合是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 字符串转 Long
     */
    public static Long toLong(String str) {
        if (isEmpty(str)) {
            return null;
        }
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 字符串转 Integer
     */
    public static Integer toInt(String str) {
        if (isEmpty(str)) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 字符串脱敏
     */
    public static String mask(String str, int start, int end, char maskChar) {
        if (isEmpty(str)) {
            return str;
        }
        if (start < 0 || end >= str.length() || start > end) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        for (int i = start; i <= end; i++) {
            sb.setCharAt(i, maskChar);
        }
        return sb.toString();
    }

    /**
     * 手机号脱敏
     */
    public static String maskPhone(String phone) {
        if (isEmpty(phone) || phone.length() != 11) {
            return phone;
        }
        return mask(phone, 3, 7, '*');
    }

    /**
     * 邮箱脱敏
     */
    public static String maskEmail(String email) {
        if (isEmpty(email) || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return mask(email, 1, atIndex - 1, '*');
    }
}
