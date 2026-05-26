package com.admin.system.service;

import com.admin.system.dto.AddMenuDTO;
import com.admin.system.dto.UpdateMenuDTO;
import com.admin.system.entity.SysMenu;
import com.admin.system.query.SysMenuQuery;
import com.admin.system.vo.SysMenuVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单权限服务接口
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 菜单分页查询
     */
    IPage<SysMenuVO> page(SysMenuQuery query);

    /**
     * 根据ID查询菜单
     */
    SysMenuVO getById(Long id);

    /**
     * 新增菜单
     */
    void add(AddMenuDTO dto);

    /**
     * 修改菜单
     */
    void update(UpdateMenuDTO dto);

    /**
     * 删除菜单
     */
    void delete(Long id);

    /**
     * 获取菜单下拉树
     */
    List<SysMenuVO> treeselect();

    /**
     * 根据角色ID获取菜单下拉树
     */
    List<SysMenuVO> getRoleMenuTreeselect(Long roleId);

    /**
     * 根据用户ID查询权限列表
     */
    List<String> getPermissionsByUserId(Long userId);

    /**
     * 校验菜单名称是否唯一
     */
    boolean isMenuNameUnique(String menuName, Long parentId);
}
