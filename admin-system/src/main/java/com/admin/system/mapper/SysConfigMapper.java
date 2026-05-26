package com.admin.system.mapper;

import com.admin.system.entity.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 参数配置 Mapper 接口
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 根据配置key查询配置值
     */
    @Select("SELECT config_value FROM sys_config WHERE config_key = #{configKey}")
    String selectValueByKey(@Param("configKey") String configKey);
}
