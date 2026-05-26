package com.admin.system.service;

import com.admin.system.dto.AddDictDataDTO;
import com.admin.system.dto.UpdateDictDataDTO;
import com.admin.system.entity.SysDictData;
import com.admin.system.query.SysDictDataQuery;
import com.admin.system.vo.SysDictDataVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 字典数据服务接口
 */
public interface ISysDictDataService extends IService<SysDictData> {

    /**
     * 字典数据分页查询
     */
    IPage<SysDictDataVO> page(SysDictDataQuery query);

    /**
     * 根据ID查询字典数据
     */
    SysDictDataVO getById(Long id);

    /**
     * 根据字典类型查询字典数据
     */
    List<SysDictDataVO> getByDictType(String dictType);

    /**
     * 新增字典数据
     */
    void add(AddDictDataDTO dto);

    /**
     * 修改字典数据
     */
    void update(UpdateDictDataDTO dto);

    /**
     * 删除字典数据
     */
    void delete(Long id);

    /**
     * 批量删除字典数据
     */
    void deleteBatch(Long[] ids);
}
