package com.admin.system.mapper;

import com.admin.system.entity.SysDictData;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 字典数据 Mapper 接口
 */
@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {

    /**
     * 根据字典类型查询字典数据
     */
    @Select("SELECT * FROM sys_dict_data WHERE dict_type = #{dictType} AND status = 1 ORDER BY dict_sort")
    List<SysDictData> selectByDictType(@Param("dictType") String dictType);
}
