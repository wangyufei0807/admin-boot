package com.admin.common.aspect;

import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.context.UserContext;
import com.admin.common.enums.Logical;
import com.admin.common.enums.ResponseCode;
import com.admin.common.exception.BusinessException;
import com.admin.common.security.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 权限校验切面
 */
@Slf4j
@Aspect
@Component
public class PermissionAspect {

    @Pointcut("@annotation(com.admin.common.annotation.RequiresPermissions)")
    public void permissionPointcut() {
    }

    @Around("permissionPointcut() && @annotation(requiresPermissions)")
    public Object around(ProceedingJoinPoint joinPoint,
                       RequiresPermissions requiresPermissions) throws Throwable {
        // 获取当前登录用户
        LoginUser loginUser = UserContext.getUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        // 获取注解上的权限标识
        String[] perms = requiresPermissions.value();
        Logical logical = requiresPermissions.logical();

        // 校验权限
        boolean hasPermission = checkPermissions(loginUser.getPermissions(), perms, logical);
        if (!hasPermission) {
            log.warn("User {} has no permission to access {}",
                    loginUser.getUsername(), joinPoint.getSignature().getName());
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }

        return joinPoint.proceed();
    }

    private boolean checkPermissions(List<String> userPermissions,
                                     String[] requiredPermissions,
                                     Logical logical) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        if (logical == Logical.AND) {
            // 需要满足所有权限
            for (String perm : requiredPermissions) {
                if (!userPermissions.contains(perm)) {
                    return false;
                }
            }
            return true;
        } else {
            // 满足任一权限即可
            for (String perm : requiredPermissions) {
                if (userPermissions.contains(perm)) {
                    return true;
                }
            }
            return false;
        }
    }
}
