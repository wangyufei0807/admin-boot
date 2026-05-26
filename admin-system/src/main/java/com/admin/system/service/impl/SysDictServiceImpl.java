package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysDict;
import com.admin.system.mapper.SysDictMapper;
import com.admin.system.service.ISysDictService;
import com.admin.system.dto.*;
import com.admin.system.query.SysDictQuery;
import com.admin.system.vo.SysDictVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典类型服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements ISysDictService {

    private final SysDictMapper dictMapper;

    @Override
    public IPage<SysDictVO> page(SysDictQuery query) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getDictName()), SysDict::getDictName, query.getDictName())
               .like(StringUtils.isNotBlank(query.getDictType()), SysDict::getDictType, query.getDictType())
               .eq(query.getStatus() != null, SysDict::getStatus, query.getStatus())
               .orderByDesc(SysDict::getCreateTime);

        IPage<SysDict> page = dictMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public SysDictVO getById(Long id) {
        SysDict dict = dictMapper.selectById(id);
        return dict != null ? toVO(dict) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddDictDTO dto) {
        SysDict dict = new SysDict();
        dict.setDictName(dto.getDictName());
        dict.setDictType(dto.getDictType());
        dict.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dict.setRemark(dto.getRemark());
        dictMapper.insert(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateDictDTO dto) {
        SysDict dict = dictMapper.selectById(dto.getId());
        if (dict == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "字典类型不存在");
        }

        dict.setDictName(dto.getDictName());
        dict.setDictType(dto.getDictType());
        if (dto.getStatus() != null) {
            dict.setStatus(dto.getStatus());
        }
        dict.setRemark(dto.getRemark());
        dictMapper.updateById(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        dictMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(Long[] ids) {
        if (ids != null) {
            dictMapper.deleteBatchIds(java.util.Arrays.asList(ids));
        }
    }

    @Override
    public boolean isDictTypeUnique(String dictType) {
        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictType, dictType);
        return dictMapper.selectCount(wrapper) == 0;
    }

    /**
     * 转换为VO
     */
    private SysDictVO toVO(SysDict dict) {
        SysDictVO vo = new SysDictVO();
        vo.setId(dict.getId());
        vo.setDictName(dict.getDictName());
        vo.setDictType(dict.getDictType());
        vo.setStatus(dict.getStatus());
        vo.setCreateTime(dict.getCreateTime());
        vo.setRemark(dict.getRemark());
        return vo;
    }
}
