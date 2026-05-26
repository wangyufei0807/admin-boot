package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysRole;
import com.admin.system.entity.SysRoleMenu;
import com.admin.system.entity.SysUserRole;
import com.admin.system.mapper.SysRoleMapper;
import com.admin.system.mapper.SysRoleMenuMapper;
import com.admin.system.mapper.SysUserRoleMapper;
import com.admin.system.service.ISysRoleService;
import com.admin.system.dto.*;
import com.admin.system.query.SysRoleQuery;
import com.admin.system.vo.SysRoleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final SysRoleMapper roleMapper;
    private final SysRoleMenuMapper roleMenuMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public IPage<SysRoleVO> page(SysRoleQuery query) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getRoleName()), SysRole::getRoleName, query.getRoleName())
               .like(StringUtils.isNotBlank(query.getRoleKey()), SysRole::getRoleKey, query.getRoleKey())
               .eq(query.getStatus() != null, SysRole::getStatus, query.getStatus())
               .orderByAsc(SysRole::getRoleSort);

        IPage<SysRole> page = roleMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public SysRoleVO getById(Long id) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            return null;
        }
        SysRoleVO vo = toVO(role);

        // 查询菜单ID列表
        List<Long> menuIds = roleMenuMapper.selectMenuIdsByRoleId(id);
        vo.setMenuIds(menuIds);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddRoleDTO dto) {
        SysRole role = new SysRole();
        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setRoleSort(dto.getRoleSort());
        role.setMenuCheckStrictly(dto.getMenuCheckStrictly() != null ? dto.getMenuCheckStrictly() : 1);
        role.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        role.setRemark(dto.getRemark());
        roleMapper.insert(role);

        // 保存角色菜单关联
        if (dto.getMenuIds() != null && dto.getMenuIds().length > 0) {
            saveRoleMenus(role.getId(), dto.getMenuIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateRoleDTO dto) {
        SysRole role = roleMapper.selectById(dto.getId());
        if (role == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "角色不存在");
        }

        role.setRoleName(dto.getRoleName());
        role.setRoleKey(dto.getRoleKey());
        role.setRoleSort(dto.getRoleSort());
        role.setMenuCheckStrictly(dto.getMenuCheckStrictly() != null ? dto.getMenuCheckStrictly() : 1);
        if (dto.getStatus() != null) {
            role.setStatus(dto.getStatus());
        }
        role.setRemark(dto.getRemark());
        roleMapper.updateById(role);

        // 更新角色菜单关联
        roleMenuMapper.deleteByRoleId(dto.getId());
        if (dto.getMenuIds() != null && dto.getMenuIds().length > 0) {
            saveRoleMenus(dto.getId(), dto.getMenuIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查是否有用户使用该角色
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getRoleId, id);
        long count = userRoleMapper.selectCount(urWrapper);
        if (count > 0) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "该角色已被用户使用，不能删除");
        }

        roleMapper.deleteById(id);
        roleMenuMapper.deleteByRoleId(id);
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
    public void updateStatus(Long id, Integer status) {
        SysRole role = roleMapper.selectById(id);
        if (role == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "角色不存在");
        }
        role.setStatus(status);
        roleMapper.updateById(role);
    }

    @Override
    public List<SysRoleVO> getByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(SysUserRole::getUserId, userId);
        List<SysUserRole> userRoles = userRoleMapper.selectList(urWrapper);

        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.in(SysRole::getId, roleIds);
        List<SysRole> roles = roleMapper.selectList(roleWrapper);

        return roles.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<SysRoleVO> listAll() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1)
               .eq(SysRole::getDelFlag, 0)
               .orderByAsc(SysRole::getRoleSort);
        List<SysRole> roles = roleMapper.selectList(wrapper);
        return roles.stream().map(this::toVO).collect(Collectors.toList());
    }

    /**
     * 保存角色菜单关联
     */
    private void saveRoleMenus(Long roleId, Long[] menuIds) {
        for (Long menuId : menuIds) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    /**
     * 转换为VO
     */
    private SysRoleVO toVO(SysRole role) {
        SysRoleVO vo = new SysRoleVO();
        vo.setId(role.getId());
        vo.setRoleName(role.getRoleName());
        vo.setRoleKey(role.getRoleKey());
        vo.setRoleSort(role.getRoleSort());
        vo.setMenuCheckStrictly(role.getMenuCheckStrictly());
        vo.setStatus(role.getStatus());
        vo.setCreateTime(role.getCreateTime());
        vo.setRemark(role.getRemark());
        return vo;
    }
}
