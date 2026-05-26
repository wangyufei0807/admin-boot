package com.admin.common.enums;

/**
 * 业务操作类型枚举
 */
public enum BusinessType {
    OTHER(0, "其他"),
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),
    GRANT(4, "授权"),
    EXPORT(5, "导出"),
    IMPORT(6, "导入"),
    QUERY(7, "查询"),
    GEN_CODE(8, "生成代码"),
    CLEAN(9, "清空数据");

    private final int code;
    private final String desc;

    BusinessType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
