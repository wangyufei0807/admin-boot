package com.admin.generator;

import com.admin.generator.config.GeneratorConfig;
import com.admin.generator.config.GeneratorConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 代码生成器启动类
 *
 * @author admin-boot
 */
@Slf4j
@SpringBootApplication
@MapperScan({"com.admin.generator.mapper"})
public class GeneratorApplication {

    public static void main(String[] args) {
        log.info("开始生成代码...");

        try {
            // 加载配置
            GeneratorConfig config = GeneratorConfigLoader.load();

            // 生成代码
            CodeGenerator.generate(config);

            log.info("代码生成完成！");
        } catch (Exception e) {
            log.error("代码生成失败", e);
        }

        // 生成完成后退出
        System.exit(0);
    }
}