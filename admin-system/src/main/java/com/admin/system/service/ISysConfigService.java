package com.admin.system.service;

import com.admin.system.dto.AddConfigDTO;
import com.admin.system.dto.UpdateConfigDTO;
import com.admin.system.entity.SysConfig;
import com.admin.system.query.SysConfigQuery;
import com.admin.system.vo.SysConfigVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 参数配置服务接口
 */
public interface ISysConfigService extends IService<SysConfig> {

    /**
     * 参数配置分页查询
     */
    IPage<SysConfigVO> page(SysConfigQuery query);

    /**
     * 根据ID查询参数配置
     */
    SysConfigVO getById(Long id);

    /**
     * 根据配置key查询配置值
     */
    String getValueByKey(String configKey);

    /**
     * 新增参数配置
     */
    void add(AddConfigDTO dto);

    /**
     * 修改参数配置
     */
    void update(UpdateConfigDTO dto);

    /**
     * 删除参数配置
     */
    void delete(Long id);

    /**
     * 批量删除参数配置
     */
    void deleteBatch(Long[] ids);

    /**
     * 获取所有配置
     */
    List<SysConfigVO> listAll();

    /**
     * 校验配置key是否唯一
     */
    boolean isConfigKeyUnique(String configKey);
}
