package com.admin.system.service;

import com.admin.system.dto.*;
import com.admin.system.entity.SysUser;
import com.admin.system.query.SysUserQuery;
import com.admin.system.vo.LoginVO;
import com.admin.system.vo.SysUserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface ISysUserService extends IService<SysUser> {

    /**
     * 用户分页查询
     */
    IPage<SysUserVO> page(SysUserQuery query);

    /**
     * 根据ID查询用户
     */
    SysUserVO getById(Long id);

    /**
     * 新增用户
     */
    void add(AddUserDTO dto);

    /**
     * 修改用户
     */
    void update(UpdateUserDTO dto);

    /**
     * 删除用户
     */
    void delete(Long id);

    /**
     * 批量删除用户
     */
    void deleteBatch(Long[] ids);

    /**
     * 修改密码
     */
    void updatePassword(UpdatePasswordDTO dto);

    /**
     * 修改头像
     */
    void updateAvatar(UpdateAvatarDTO dto);

    /**
     * 修改个人信息
     */
    void updateProfile(UpdateProfileDTO dto);

    /**
     * 根据用户名查询用户
     */
    SysUserVO getByUsername(String username);

    /**
     * 校验用户名是否唯一
     */
    boolean isUsernameUnique(String username);

    /**
     * 校验手机号是否唯一
     */
    boolean isPhoneUnique(String phone);
}
