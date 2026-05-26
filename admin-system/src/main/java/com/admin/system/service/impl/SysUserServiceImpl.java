package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.base.PageQuery;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysUser;
import com.admin.system.entity.SysUserRole;
import com.admin.system.mapper.SysMenuMapper;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.mapper.SysUserRoleMapper;
import com.admin.system.service.ISysUserService;
import com.admin.system.dto.*;
import com.admin.system.query.SysUserQuery;
import com.admin.system.vo.SysUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final SysMenuMapper menuMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public IPage<SysUserVO> page(SysUserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getUsername()), SysUser::getUsername, query.getUsername())
               .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
               .like(StringUtils.isNotBlank(query.getPhone()), SysUser::getPhone, query.getPhone())
               .orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> page = userMapper.selectPage(query.toPage(), wrapper);

        // 优化：批量查询用户角色，解决 N+1 问题
        List<Long> userIds = page.getRecords().stream().map(SysUser::getId).collect(Collectors.toList());
        Map<Long, List<Long>> userRoleMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<SysUserRole> allUserRoles = userRoleMapper.selectByUserIds(userIds);
            for (SysUserRole ur : allUserRoles) {
                userRoleMap.computeIfAbsent(ur.getUserId(), k -> new ArrayList<>()).add(ur.getRoleId());
            }
        }

        return page.convert(user -> toVO(user, userRoleMap.get(user.getId())));
    }

    @Override
    public SysUserVO getById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            return null;
        }
        return toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddUserDTO dto) {
        // 校验用户名唯一
        if (!isUsernameUnique(dto.getUsername())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户名已存在");
        }

        // 校验手机号唯一
        if (StringUtils.isNotBlank(dto.getPhone()) && !isPhoneUnique(dto.getPhone())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "手机号已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        user.setNickName(dto.getNickName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setSex(dto.getSex());
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        user.setRemark(dto.getRemark());
        userMapper.insert(user);

        // 保存用户角色关联
        if (dto.getRoleIds() != null && dto.getRoleIds().length > 0) {
            saveUserRoles(user.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateUserDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户不存在");
        }

        // 校验手机号唯一
        if (StringUtils.isNotBlank(dto.getPhone()) && !isPhoneUnique(dto.getPhone())) {
            if (!user.getPhone().equals(dto.getPhone())) {
                throw new BusinessException(ResponseCode.BAD_REQUEST, "手机号已存在");
            }
        }

        user.setNickName(dto.getNickName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setSex(dto.getSex());
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        user.setRemark(dto.getRemark());
        userMapper.updateById(user);

        // 更新用户角色关联
        userRoleMapper.deleteByUserId(dto.getId());
        if (dto.getRoleIds() != null && dto.getRoleIds().length > 0) {
            saveUserRoles(dto.getId(), dto.getRoleIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        if (id == 1L) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "不能删除超级管理员");
        }
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                delete(id);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(UpdatePasswordDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "旧密码不正确");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAvatar(UpdateAvatarDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户不存在");
        }
        user.setAvatar(dto.getAvatar());
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UpdateProfileDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "用户不存在");
        }

        // 校验手机号唯一
        if (StringUtils.isNotBlank(dto.getPhone()) && !isPhoneUnique(dto.getPhone())) {
            if (!user.getPhone().equals(dto.getPhone())) {
                throw new BusinessException(ResponseCode.BAD_REQUEST, "手机号已存在");
            }
        }

        user.setNickName(dto.getNickName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setSex(dto.getSex());
        userMapper.updateById(user);
    }

    @Override
    public SysUserVO getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);
        return user != null ? toVO(user) : null;
    }

    @Override
    public boolean isUsernameUnique(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return userMapper.selectCount(wrapper) == 0;
    }

    @Override
    public boolean isPhoneUnique(String phone) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getPhone, phone);
        return userMapper.selectCount(wrapper) == 0;
    }

    /**
     * 保存用户角色关联
     */
    private void saveUserRoles(Long userId, Long[] roleIds) {
        for (Long roleId : roleIds) {
            com.admin.system.entity.SysUserRole userRole = new com.admin.system.entity.SysUserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    /**
     * 转换为VO（单条查询场景）
     */
    private SysUserVO toVO(SysUser user) {
        return toVO(user, null);
    }

    /**
     * 转换为VO（支持预加载角色列表）
     */
    private SysUserVO toVO(SysUser user, List<Long> roleIds) {
        SysUserVO vo = new SysUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickName(user.getNickName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setSex(user.getSex());
        vo.setStatus(user.getStatus());
        vo.setLoginIp(user.getLoginIp());
        vo.setLoginTime(user.getLoginTime());
        vo.setCreateTime(user.getCreateTime());
        vo.setRemark(user.getRemark());

        // 如果没有预加载角色列表，则单独查询
        if (roleIds == null) {
            LambdaQueryWrapper<com.admin.system.entity.SysUserRole> urWrapper = new LambdaQueryWrapper<>();
            urWrapper.eq(com.admin.system.entity.SysUserRole::getUserId, user.getId());
            List<com.admin.system.entity.SysUserRole> userRoles = userRoleMapper.selectList(urWrapper);
            if (userRoles != null && !userRoles.isEmpty()) {
                vo.setRoleIds(userRoles.stream()
                        .map(com.admin.system.entity.SysUserRole::getRoleId)
                        .collect(Collectors.toList()));
            }
        } else {
            vo.setRoleIds(roleIds);
        }

        return vo;
    }
}
