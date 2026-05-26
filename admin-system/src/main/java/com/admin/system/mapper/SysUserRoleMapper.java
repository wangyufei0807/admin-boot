package com.admin.system.mapper;

import com.admin.system.entity.SysUserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色 Mapper 接口
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 根据用户ID删除
     */
    @Select("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID删除
     */
    @Select("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量查询用户角色（解决 N+1 问题）
     */
    @Select("<script>SELECT * FROM sys_user_role WHERE user_id IN <foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    List<SysUserRole> selectByUserIds(@Param("userIds") List<Long> userIds);
}
