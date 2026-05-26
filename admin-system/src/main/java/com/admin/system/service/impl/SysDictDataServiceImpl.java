package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysDictData;
import com.admin.system.mapper.SysDictDataMapper;
import com.admin.system.service.ISysDictDataService;
import com.admin.system.dto.*;
import com.admin.system.query.SysDictDataQuery;
import com.admin.system.vo.SysDictDataVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 字典数据服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements ISysDictDataService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    public IPage<SysDictDataVO> page(SysDictDataQuery query) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(query.getDictType()), SysDictData::getDictType, query.getDictType())
               .like(StringUtils.isNotBlank(query.getDictLabel()), SysDictData::getDictLabel, query.getDictLabel())
               .eq(query.getStatus() != null, SysDictData::getStatus, query.getStatus())
               .orderByAsc(SysDictData::getDictSort);

        IPage<SysDictData> page = dictDataMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public SysDictDataVO getById(Long id) {
        SysDictData dictData = dictDataMapper.selectById(id);
        return dictData != null ? toVO(dictData) : null;
    }

    @Override
    @Cacheable(value = "sysDictData", key = "#dictType")
    public List<SysDictDataVO> getByDictType(String dictType) {
        List<SysDictData> list = dictDataMapper.selectByDictType(dictType);
        return list.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysDictData", allEntries = true)
    public void add(AddDictDataDTO dto) {
        SysDictData dictData = new SysDictData();
        dictData.setDictSort(dto.getDictSort() != null ? dto.getDictSort() : 0);
        dictData.setDictLabel(dto.getDictLabel());
        dictData.setDictValue(dto.getDictValue());
        dictData.setDictType(dto.getDictType());
        dictData.setCssClass(dto.getCssClass());
        dictData.setListClass(dto.getListClass());
        dictData.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : 0);
        dictData.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);
        dictData.setRemark(dto.getRemark());
        dictDataMapper.insert(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysDictData", allEntries = true)
    public void update(UpdateDictDataDTO dto) {
        SysDictData dictData = dictDataMapper.selectById(dto.getId());
        if (dictData == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "字典数据不存在");
        }

        dictData.setDictSort(dto.getDictSort());
        dictData.setDictLabel(dto.getDictLabel());
        dictData.setDictValue(dto.getDictValue());
        dictData.setDictType(dto.getDictType());
        dictData.setCssClass(dto.getCssClass());
        dictData.setListClass(dto.getListClass());
        if (dto.getIsDefault() != null) {
            dictData.setIsDefault(dto.getIsDefault());
        }
        if (dto.getStatus() != null) {
            dictData.setStatus(dto.getStatus());
        }
        dictData.setRemark(dto.getRemark());
        dictDataMapper.updateById(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysDictData", allEntries = true)
    public void delete(Long id) {
        dictDataMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysDictData", allEntries = true)
    public void deleteBatch(Long[] ids) {
        if (ids != null) {
            dictDataMapper.deleteBatchIds(Arrays.asList(ids));
        }
    }

    /**
     * 转换为VO
     */
    private SysDictDataVO toVO(SysDictData dictData) {
        SysDictDataVO vo = new SysDictDataVO();
        vo.setId(dictData.getId());
        vo.setDictSort(dictData.getDictSort());
        vo.setDictLabel(dictData.getDictLabel());
        vo.setDictValue(dictData.getDictValue());
        vo.setDictType(dictData.getDictType());
        vo.setCssClass(dictData.getCssClass());
        vo.setListClass(dictData.getListClass());
        vo.setIsDefault(dictData.getIsDefault());
        vo.setStatus(dictData.getStatus());
        vo.setCreateTime(dictData.getCreateTime());
        vo.setRemark(dictData.getRemark());
        return vo;
    }
}
