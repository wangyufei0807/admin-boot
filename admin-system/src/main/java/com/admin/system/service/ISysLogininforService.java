package com.admin.system.service;

import com.admin.system.entity.SysLogininfor;
import com.admin.system.query.SysLogininforQuery;
import com.admin.system.vo.SysLogininforVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 系统访问记录服务接口
 */
public interface ISysLogininforService extends IService<SysLogininfor> {

    /**
     * 登录日志分页查询
     */
    IPage<SysLogininforVO> page(SysLogininforQuery query);

    /**
     * 删除登录日志
     */
    void delete(Long id);

    /**
     * 批量删除登录日志
     */
    void deleteBatch(Long[] ids);

    /**
     * 清空登录日志
     */
    void clean();

    /**
     * 记录登录日志
     */
    void recordLogininfor(Long userId, String username, String ipaddr, String msg, Integer loginStatus);
}
