package com.admin.common.aspect;

import com.admin.common.annotation.Log;
import com.admin.common.context.UserContext;
import com.admin.common.enums.BusinessType;
import com.admin.common.enums.ResponseCode;
import com.admin.common.exception.BusinessException;
import com.admin.common.result.R;
import com.admin.common.security.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    private final ObjectMapper objectMapper;

    public LogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Pointcut("@annotation(com.admin.common.annotation.Log)")
    public void logPointcut() {
    }

    @Around("logPointcut() && @annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, Log operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法参数（使用参数名而不是 args0、args1）
        Object[] args = joinPoint.getArgs();
        String operParam = null;
        if (operationLog.saveParam() && args != null && args.length > 0) {
            try {
                operParam = buildParamString(joinPoint, args);
            } catch (Exception e) {
                log.warn("Failed to serialize operation params", e);
            }
        }

        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();

        // 执行目标方法
        Object result = null;
        int operStatus = 0;
        String errorMsg = null;

        try {
            result = joinPoint.proceed();
            operStatus = 0;
        } catch (Exception e) {
            operStatus = 1;
            errorMsg = e.getMessage();
            throw e;
        }

        // 保存操作日志（异步）
        long costTime = System.currentTimeMillis() - startTime;
        try {
            saveOperLog(joinPoint, operationLog, request, operParam, operStatus, errorMsg, costTime, result);
        } catch (Exception e) {
            log.error("Save operation log failed", e);
        }

        return result;
    }

    private void saveOperLog(ProceedingJoinPoint joinPoint, Log operationLog,
                            HttpServletRequest request, String operParam,
                            int operStatus, String errorMsg, long costTime, Object result) {
        try {
            // 获取登录用户
            LoginUser loginUser = UserContext.getUser();
            Long userId = loginUser != null ? loginUser.getUserId() : null;
            String userName = loginUser != null ? loginUser.getUsername() : null;

            // 获取 IP 地址
            String ipaddr = getIpAddress(request);

            // 获取返回结果
            String jsonResult = null;
            if (operationLog.saveResult() && result instanceof R<?> r) {
                try {
                    jsonResult = objectMapper.writeValueAsString(r);
                } catch (Exception e) {
                    log.warn("Failed to serialize result", e);
                }
            }

            // 构建日志信息
            String method = joinPoint.getSignature().getDeclaringTypeName() + "." +
                    joinPoint.getSignature().getName();
            String requestMethod = request != null ? request.getMethod() : "UNKNOWN";

            // 业务类型
            int businessType = operationLog.businessType() != null
                    ? operationLog.businessType().getCode()
                    : BusinessType.OTHER.getCode();

            // 输出日志
            log.info("Operation log - title: {}, businessType: {}, method: {}, requestMethod: {}, " +
                            "userId: {}, userName: {}, ipaddr: {}, operParam: {}, operStatus: {}, " +
                            "errorMsg: {}, costTime: {}ms",
                    operationLog.title(), businessType, method, requestMethod,
                    userId, userName, ipaddr, operParam, operStatus,
                    errorMsg, costTime);

        } catch (Exception e) {
            log.error("Save operation log error", e);
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0.0.0.0".equals(ip) ? "127.0.0.1" : ip;
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 构建参数字符串（将查询对象字段平铺）
     */
    private String buildParamString(ProceedingJoinPoint joinPoint, Object[] args) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            java.lang.reflect.Parameter[] parameters = method.getParameters();

            Map<String, Object> paramMap = new HashMap<>();
            for (int i = 0; i < parameters.length && i < args.length; i++) {
                Object arg = args[i];
                // 跳过 HttpServletRequest 等不可序列化的参数
                if (arg instanceof HttpServletRequest) {
                    continue;
                }
                // 将参数对象的字段平铺到顶层
                if (arg != null && !isPrimitiveOrWrapper(arg.getClass())) {
                    Map<String, Object> fields = objectMapper.convertValue(arg, Map.class);
                    if (fields != null) {
                        paramMap.putAll(fields);
                    }
                } else {
                    String paramName = parameters[i].getName();
                    paramMap.put(paramName, arg);
                }
            }
            return objectMapper.writeValueAsString(paramMap);
        } catch (Exception e) {
            // 降级处理：直接返回简单描述
            return "[" + args.length + " parameters]";
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() ||
               type == Boolean.class ||
               type == Integer.class ||
               type == Long.class ||
               type == Double.class ||
               type == Float.class ||
               type == Short.class ||
               type == Byte.class ||
               type == Character.class ||
               type == String.class;
    }
}
