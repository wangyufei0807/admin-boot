package com.admin.generator.engine;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Freemarker 模板引擎
 */
@Slf4j
public class FreemarkerEngine {

    private final Configuration configuration;

    public FreemarkerEngine() {
        configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        configuration.setDefaultEncoding("UTF-8");
    }

    public void generate(String templateName, Object data, String outputPath) {
        try {
            Template template = configuration.getTemplate(templateName);
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            Writer out = new FileWriter(outputFile);
            template.process(data, out);
            out.close();
            log.info("生成文件: {}", outputPath);
        } catch (IOException | TemplateException e) {
            log.error("生成文件失败: {}", outputPath, e);
        }
    }

    public void generate(String templateName, Map<String, Object> data, String outputPath) {
        try {
            Template template = configuration.getTemplate(templateName);
            File outputFile = new File(outputPath);
            outputFile.getParentFile().mkdirs();
            Writer out = new FileWriter(outputFile);
            template.process(data, out);
            out.close();
            log.info("生成文件: {}", outputPath);
        } catch (IOException | TemplateException e) {
            log.error("生成文件失败: {}", outputPath, e);
        }
    }
}