package com.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Admin Boot 启动类
 *
 * @author admin-boot
 */
@SpringBootApplication(scanBasePackages = {"com.admin"})
@MapperScan({"com.admin.system.mapper"})
public class AdminBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminBootApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  Admin Boot 启动成功   ლ(´ڡ`ლ)ﾞ");
    }
}