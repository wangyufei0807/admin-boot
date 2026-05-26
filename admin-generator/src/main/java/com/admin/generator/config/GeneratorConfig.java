package com.admin.generator.config;

import lombok.Data;

/**
 * 生成器总配置
 */
@Data
public class GeneratorConfig {
    private JdbcConfig jdbc;
    private ProjectConfig project;
    private OutputConfig output;
    private GenerateConfig generate;
    private EntityConfig entity;
    private ControllerConfig controller;
    private MapperConfig mapper;
}