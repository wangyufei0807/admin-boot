package com.admin.common.config;

import com.admin.common.context.UserContext;
import com.admin.common.security.LoginUser;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 */
@Slf4j
@Component
public class MetaObjectHandlerImpl implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取当前登录用户
        String username = getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String username = getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, now);
        this.strictUpdateFill(metaObject, "updateBy", String.class, username);
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        try {
            LoginUser loginUser = UserContext.getUser();
            return loginUser != null ? loginUser.getUsername() : "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
