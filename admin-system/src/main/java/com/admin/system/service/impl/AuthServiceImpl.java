package com.admin.system.service.impl;

import com.admin.common.context.UserContext;
import com.admin.common.exception.BusinessException;
import com.admin.common.security.JwtTokenUtil;
import com.admin.common.security.LoginUser;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.IpUtils;
import com.admin.system.dto.*;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysMenuMapper;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.service.IAuthService;
import com.admin.system.service.ISysMenuService;
import com.admin.system.service.ISysUserService;
import com.admin.system.vo.LoginVO;
import com.admin.system.vo.UserInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final ISysUserService userService;
    private final ISysMenuService menuService;
    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO loginDTO, HttpServletRequest request) {
        // 查询用户
        SysUser user = userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, loginDTO.getUsername())
        );

        if (user == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户已被禁用");
        }

        // 更新登录信息（获取真实IP）
        user.setLoginIp(IpUtils.getIpAddress(request));
        user.setLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        // 构建LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickName(user.getNickName());
        loginUser.setAvatar(user.getAvatar());

        // 获取角色和权限
        List<String> roles = new ArrayList<>();
        List<String> permissions = menuService.getPermissionsByUserId(user.getId());
        loginUser.setRoles(roles);
        loginUser.setPermissions(permissions);

        // 生成Token
        String accessToken = jwtTokenUtil.generateAccessToken(loginUser);
        String refreshToken = jwtTokenUtil.generateRefreshToken(loginUser);

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(accessToken);
        loginVO.setRefreshToken(refreshToken);
        loginVO.setExpiresIn(jwtTokenUtil.getAccessTokenExpireTime());
        loginVO.setTokenType("Bearer");
        loginVO.setUserInfo(buildUserInfo(loginUser));

        return loginVO;
    }

    @Override
    public LoginVO refresh(RefreshTokenDTO refreshTokenDTO) {
        String refreshToken = refreshTokenDTO.getRefreshToken();

        // 验证并消费 Refresh Token（一次性使用，使用后立即失效）
        if (!jwtTokenUtil.validateAndConsumeRefreshToken(refreshToken)) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "刷新令牌无效或已失效");
        }

        // 解析用户信息
        Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);

        // 重新获取用户信息
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getStatus() != 1) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "用户不存在或已被禁用");
        }

        // 构建 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setPassword(user.getPassword());
        loginUser.setNickName(user.getNickName());
        loginUser.setAvatar(user.getAvatar());
        loginUser.setPermissions(menuService.getPermissionsByUserId(user.getId()));

        // 生成新Token
        String newAccessToken = jwtTokenUtil.generateAccessToken(loginUser);
        String newRefreshToken = jwtTokenUtil.generateRefreshToken(loginUser);

        // 构建响应
        LoginVO loginVO = new LoginVO();
        loginVO.setAccessToken(newAccessToken);
        loginVO.setRefreshToken(newRefreshToken);
        loginVO.setExpiresIn(jwtTokenUtil.getAccessTokenExpireTime());
        loginVO.setTokenType("Bearer");
        loginVO.setUserInfo(buildUserInfo(loginUser));

        return loginVO;
    }

    @Override
    public void logout() {
        LoginUser loginUser = UserContext.getUser();
        if (loginUser != null) {
            jwtTokenUtil.removeToken(loginUser.getUserId());
        }
    }

    @Override
    public LoginVO getCurrentUser() {
        LoginUser loginUser = UserContext.getUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED, "未登录");
        }

        LoginVO loginVO = new LoginVO();
        loginVO.setUserInfo(buildUserInfo(loginUser));
        return loginVO;
    }

    /**
     * 构建用户信息
     */
    private UserInfoVO buildUserInfo(LoginUser loginUser) {
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(loginUser.getUserId());
        userInfo.setUsername(loginUser.getUsername());
        userInfo.setNickName(loginUser.getNickName());
        userInfo.setAvatar(loginUser.getAvatar());
        userInfo.setRoles(loginUser.getRoles());
        userInfo.setPermissions(loginUser.getPermissions());
        return userInfo;
    }
}
