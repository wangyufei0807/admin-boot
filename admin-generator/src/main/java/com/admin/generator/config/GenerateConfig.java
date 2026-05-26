package com.admin.generator.config;

import lombok.Data;

/**
 * 生成配置
 */
@Data
public class GenerateConfig {
    private String tableNames;
    private Boolean entity;
    private Boolean controller;
    private Boolean service;
    private Boolean serviceImpl;
    private Boolean mapper;
    private Boolean mapperXml;
    private Boolean dto;
    private Boolean query;
    private Boolean vo;
    private Boolean converter;
}
