package com.admin.generator;

import com.admin.generator.config.GeneratorConfig;
import com.admin.generator.config.GeneratorConfigLoader;
import com.admin.generator.domain.TableInfo;
import com.admin.generator.engine.FreemarkerEngine;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 代码生成器
 */
@Slf4j
public class CodeGenerator {

    public static void main(String[] args) {
        // 加载配置
        GeneratorConfig config = GeneratorConfigLoader.load();

        // 生成代码
        generate(config);

        log.info("代码生成完成！");
    }

    public static void generate(GeneratorConfig config) {
        // 解析表名
        List<String> tableNames = parseTableNames(config.getGenerate().getTableNames());

        FastAutoGenerator.create(
                        config.getJdbc().getUrl(),
                        config.getJdbc().getUsername(),
                        config.getJdbc().getPassword()
                )
                .globalConfig(builder -> {
                    builder.author(config.getProject().getAuthor())
                            .outputDir(config.getOutput().getJavaPath())
                            .dateType(DateType.TIME_PACK)
                            .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> {
                    builder.parent(config.getProject().getPackageName())
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    config.getOutput().getMapperXmlPath()));
                })
                .strategyConfig(builder -> {
                    // 设置需要生成的表
                    builder.addInclude(tableNames);

                    // Entity 配置
                    builder.entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .logicDeleteColumnName("del_flag")
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel);

                    // Controller 配置
                    builder.controllerBuilder()
                            .enableRestStyle()
                            .enableHyphenStyle();

                    // Service 配置
                    builder.serviceBuilder()
                            .formatServiceFileName("I%sService")
                            .formatServiceImplFileName("%sServiceImpl");

                    // Mapper 配置
                    builder.mapperBuilder()
                            .enableBaseResultMap()
                            .enableBaseColumnList();
                })
                .templateConfig(builder -> {
                    // 禁用默认模板，使用自定义
                    builder.disable(TemplateType.CONTROLLER, TemplateType.SERVICE,
                            TemplateType.SERVICE_IMPL, TemplateType.ENTITY,
                            TemplateType.MAPPER, TemplateType.XML);
                })
                .injectionConfig(builder -> {
                    // 配置自定义参数
                    builder.customMap(Collections.singletonMap("parameterObject",
                            config.getController().getParameterObject()));
                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();

        // 生成额外文件（DTO、Query、VO）
        generateExtraFiles(config, tableNames);
    }

    /**
     * 解析表名
     */
    private static List<String> parseTableNames(String tableNames) {
        if (tableNames == null || tableNames.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] tables = tableNames.split(",");
        List<String> result = new ArrayList<>();
        for (String table : tables) {
            String trimmed = table.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    /**
     * 生成额外文件（DTO、Query、VO）
     */
    private static void generateExtraFiles(GeneratorConfig config, List<String> tableNames) {
        FreemarkerEngine engine = new FreemarkerEngine();

        for (String tableName : tableNames) {
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setEntityName(convertToEntityName(tableName,
                    config.getProject().getTablePrefix()));
            tableInfo.setPackageName(config.getProject().getPackageName());
            tableInfo.setAuthor(config.getProject().getAuthor());
            tableInfo.setDate(java.time.LocalDate.now().toString());
            tableInfo.setParameterObject(config.getController().getParameterObject());

            // 生成 DTO
            if (Boolean.TRUE.equals(config.getGenerate().getDto())) {
                engine.generate("dto.ftl", tableInfo,
                        config.getOutput().getJavaPath() + "/" +
                                config.getProject().getPackageName().replace(".", "/") +
                                "/dto/" + tableInfo.getEntityName() + "DTO.java");
            }

            // 生成 Query
            if (Boolean.TRUE.equals(config.getGenerate().getQuery())) {
                engine.generate("query.ftl", tableInfo,
                        config.getOutput().getJavaPath() + "/" +
                                config.getProject().getPackageName().replace(".", "/") +
                                "/query/" + tableInfo.getEntityName() + "Query.java");
            }

            // 生成 VO
            if (Boolean.TRUE.equals(config.getGenerate().getVo())) {
                engine.generate("vo.ftl", tableInfo,
                        config.getOutput().getJavaPath() + "/" +
                                config.getProject().getPackageName().replace(".", "/") +
                                "/vo/" + tableInfo.getEntityName() + "VO.java");
            }
        }
    }

    /**
     * 转换为实体名称
     */
    private static String convertToEntityName(String tableName, String tablePrefix) {
        if (tablePrefix != null && tableName.startsWith(tablePrefix)) {
            tableName = tableName.substring(tablePrefix.length());
        }
        // 下划线转驼峰
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (char c : tableName.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                result.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return result.toString();
    }
}
