package com.admin.system.service;

import com.admin.system.dto.LoginDTO;
import com.admin.system.dto.RefreshTokenDTO;
import com.admin.system.vo.LoginVO;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口
 */
public interface IAuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO, HttpServletRequest request);

    /**
     * 刷新Token
     */
    LoginVO refresh(RefreshTokenDTO refreshTokenDTO);

    /**
     * 用户登出
     */
    void logout();

    /**
     * 获取当前用户信息
     */
    LoginVO getCurrentUser();
}
