package com.admin.common.enums;

/**
 * 菜单类型枚举
 */
public enum MenuType {
    M("M", "目录"),
    C("C", "菜单"),
    F("F", "按钮");

    private final String code;
    private final String desc;

    MenuType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
