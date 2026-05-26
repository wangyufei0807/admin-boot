package com.admin.system.service;

import com.admin.system.entity.SysOperLog;
import com.admin.system.query.SysOperLogQuery;
import com.admin.system.vo.SysOperLogVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 操作日志服务接口
 */
public interface ISysOperLogService extends IService<SysOperLog> {

    /**
     * 操作日志分页查询
     */
    IPage<SysOperLogVO> page(SysOperLogQuery query);

    /**
     * 根据ID查询操作日志
     */
    SysOperLogVO getById(Long id);

    /**
     * 删除操作日志
     */
    void delete(Long id);

    /**
     * 批量删除操作日志
     */
    void deleteBatch(Long[] ids);

    /**
     * 清空操作日志
     */
    void clean();

    /**
     * 保存操作日志
     */
    boolean save(SysOperLog operLog);
}
