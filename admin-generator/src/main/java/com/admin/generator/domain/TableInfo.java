package com.admin.generator.domain;

import lombok.Data;

/**
 * 表信息
 */
@Data
public class TableInfo {
    private String tableName;
    private String entityName;
    private String packageName;
    private String author;
    private String date;
    private Boolean parameterObject;
}