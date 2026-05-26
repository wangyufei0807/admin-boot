package com.admin.system.service;

import com.admin.system.dto.AddDictDTO;
import com.admin.system.dto.UpdateDictDTO;
import com.admin.system.entity.SysDict;
import com.admin.system.query.SysDictQuery;
import com.admin.system.vo.SysDictVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 字典类型服务接口
 */
public interface ISysDictService extends IService<SysDict> {

    /**
     * 字典类型分页查询
     */
    IPage<SysDictVO> page(SysDictQuery query);

    /**
     * 根据ID查询字典类型
     */
    SysDictVO getById(Long id);

    /**
     * 新增字典类型
     */
    void add(AddDictDTO dto);

    /**
     * 修改字典类型
     */
    void update(UpdateDictDTO dto);

    /**
     * 删除字典类型
     */
    void delete(Long id);

    /**
     * 批量删除字典类型
     */
    void deleteBatch(Long[] ids);

    /**
     * 校验字典类型是否唯一
     */
    boolean isDictTypeUnique(String dictType);
}
