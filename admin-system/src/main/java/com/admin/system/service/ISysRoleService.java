package com.admin.system.service;

import com.admin.system.dto.AddRoleDTO;
import com.admin.system.dto.UpdateRoleDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.query.SysRoleQuery;
import com.admin.system.vo.SysRoleVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色服务接口
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 角色分页查询
     */
    IPage<SysRoleVO> page(SysRoleQuery query);

    /**
     * 根据ID查询角色
     */
    SysRoleVO getById(Long id);

    /**
     * 新增角色
     */
    void add(AddRoleDTO dto);

    /**
     * 修改角色
     */
    void update(UpdateRoleDTO dto);

    /**
     * 删除角色
     */
    void delete(Long id);

    /**
     * 批量删除角色
     */
    void deleteBatch(Long[] ids);

    /**
     * 修改角色状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRoleVO> getByUserId(Long userId);

    /**
     * 获取所有角色列表
     */
    List<SysRoleVO> listAll();
}
