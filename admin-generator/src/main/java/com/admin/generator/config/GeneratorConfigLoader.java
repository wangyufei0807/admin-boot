package com.admin.generator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * 配置加载器
 */
@Slf4j
public class GeneratorConfigLoader {

    private static final String DEFAULT_CONFIG_PATH = "src/main/resources/generator.yml";

    public static GeneratorConfig load() {
        return load(DEFAULT_CONFIG_PATH);
    }

    public static GeneratorConfig load(String configPath) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            File configFile = new File(configPath);
            if (!configFile.exists()) {
                configFile = new File(DEFAULT_CONFIG_PATH);
            }
            GeneratorConfig config = mapper.readValue(configFile, GeneratorConfig.class);
            log.info("加载配置文件: {}", configFile.getAbsolutePath());
            return config;
        } catch (IOException e) {
            log.error("加载配置文件失败", e);
            throw new RuntimeException("加载配置文件失败: " + e.getMessage());
        }
    }
}