package com.admin.common.security;

import com.admin.common.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 用户上下文填充过滤器
 * 将 SecurityContext 中的用户信息填充到 UserContext
 */
@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                UserContext.setUser(loginUser);
            }
        } catch (Exception e) {
            // ignore
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.remove();
        }
    }
}
