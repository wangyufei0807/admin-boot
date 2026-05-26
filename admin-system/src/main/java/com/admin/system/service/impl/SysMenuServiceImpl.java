package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysMenu;
import com.admin.system.mapper.SysMenuMapper;
import com.admin.system.mapper.SysRoleMenuMapper;
import com.admin.system.service.ISysMenuService;
import com.admin.system.dto.*;
import com.admin.system.query.SysMenuQuery;
import com.admin.system.vo.SysMenuVO;
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
 * 菜单权限服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    private final SysMenuMapper menuMapper;
    private final SysRoleMenuMapper roleMenuMapper;

    @Override
    public IPage<SysMenuVO> page(SysMenuQuery query) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getMenuName()), SysMenu::getMenuName, query.getMenuName())
               .eq(query.getStatus() != null, SysMenu::getStatus, query.getStatus())
               .orderByAsc(SysMenu::getOrderNum);

        IPage<SysMenu> page = menuMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public SysMenuVO getById(Long id) {
        SysMenu menu = menuMapper.selectById(id);
        return menu != null ? toVO(menu) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddMenuDTO dto) {
        SysMenu menu = new SysMenu();
        menu.setMenuName(dto.getMenuName());
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setOrderNum(dto.getOrderNum());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setQueryParam(dto.getQueryParam());
        menu.setIsFrame(dto.getIsFrame() != null ? dto.getIsFrame() : 1);
        menu.setIsCache(dto.getIsCache() != null ? dto.getIsCache() : 0);
        menu.setMenuType(dto.getMenuType());
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 0);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menu.setPerms(dto.getPerms());
        menu.setIcon(dto.getIcon());
        menu.setRemark(dto.getRemark());
        menuMapper.insert(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateMenuDTO dto) {
        SysMenu menu = menuMapper.selectById(dto.getId());
        if (menu == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "菜单不存在");
        }

        menu.setMenuName(dto.getMenuName());
        menu.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        menu.setOrderNum(dto.getOrderNum());
        menu.setPath(dto.getPath());
        menu.setComponent(dto.getComponent());
        menu.setQueryParam(dto.getQueryParam());
        menu.setIsFrame(dto.getIsFrame() != null ? dto.getIsFrame() : 1);
        menu.setIsCache(dto.getIsCache() != null ? dto.getIsCache() : 0);
        menu.setMenuType(dto.getMenuType());
        menu.setVisible(dto.getVisible() != null ? dto.getVisible() : 0);
        menu.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        menu.setPerms(dto.getPerms());
        menu.setIcon(dto.getIcon());
        menu.setRemark(dto.getRemark());
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        // 检查是否有子菜单
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        long count = menuMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "存在子菜单，无法删除");
        }

        // 删除角色菜单关联
        roleMenuMapper.deleteByMenuId(id);

        menuMapper.deleteById(id);
    }

    @Override
    public List<SysMenuVO> treeselect() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1)
               .orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return buildTree(menus);
    }

    @Override
    public List<SysMenuVO> getRoleMenuTreeselect(Long roleId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1)
               .orderByAsc(SysMenu::getOrderNum);
        List<SysMenu> menus = menuMapper.selectList(wrapper);

        // 获取角色已分配的菜单ID
        List<Long> checkedMenuIds = roleMenuMapper.selectMenuIdsByRoleId(roleId);

        List<SysMenuVO> voList = menus.stream().map(this::toVO).collect(Collectors.toList());

        // 设置选中状态
        for (SysMenuVO vo : voList) {
            if (checkedMenuIds.contains(vo.getId())) {
                vo.getClass(); // just to reference
            }
        }

        return buildTree(menus);
    }

    @Override
    public List<String> getPermissionsByUserId(Long userId) {
        return menuMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public boolean isMenuNameUnique(String menuName, Long parentId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuName, menuName)
               .eq(SysMenu::getParentId, parentId);
        return menuMapper.selectCount(wrapper) == 0;
    }

    /**
     * 构建菜单树
     */
    private List<SysMenuVO> buildTree(List<SysMenu> menus) {
        List<SysMenuVO> voList = menus.stream().map(this::toVO).collect(Collectors.toList());
        return voList.stream()
                .filter(vo -> !vo.getId().equals(vo.getParentId()))
                .peek(vo -> vo.setChildren(getChildren(vo.getId(), voList)))
                .filter(vo -> vo.getParentId() == 0 || vo.getParentId() == null)
                .collect(Collectors.toList());
    }

    private List<SysMenuVO> getChildren(Long parentId, List<SysMenuVO> voList) {
        return voList.stream()
                .filter(vo -> parentId.equals(vo.getParentId()))
                .peek(vo -> vo.setChildren(getChildren(vo.getId(), voList)))
                .collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private SysMenuVO toVO(SysMenu menu) {
        SysMenuVO vo = new SysMenuVO();
        vo.setId(menu.getId());
        vo.setMenuName(menu.getMenuName());
        vo.setParentId(menu.getParentId());
        vo.setOrderNum(menu.getOrderNum());
        vo.setPath(menu.getPath());
        vo.setComponent(menu.getComponent());
        vo.setQueryParam(menu.getQueryParam());
        vo.setIsFrame(menu.getIsFrame());
        vo.setIsCache(menu.getIsCache());
        vo.setMenuType(menu.getMenuType());
        vo.setVisible(menu.getVisible());
        vo.setStatus(menu.getStatus());
        vo.setPerms(menu.getPerms());
        vo.setIcon(menu.getIcon());
        return vo;
    }
}
