package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.R;
import com.admin.system.dto.LoginDTO;
import com.admin.system.dto.RefreshTokenDTO;
import com.admin.system.service.IAuthService;
import com.admin.system.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    @Log(title = "用户登录", businessType = BusinessType.OTHER)
    public R<LoginVO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request) {
        LoginVO loginVO = authService.login(loginDTO, request);
        return R.ok(loginVO);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public R<LoginVO> refresh(@RequestBody @Valid RefreshTokenDTO refreshTokenDTO) {
        LoginVO loginVO = authService.refresh(refreshTokenDTO);
        return R.ok(loginVO);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    @Log(title = "用户登出", businessType = BusinessType.OTHER)
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/current")
    public R<LoginVO> getCurrentUser() {
        LoginVO loginVO = authService.getCurrentUser();
        return R.ok(loginVO);
    }
}
