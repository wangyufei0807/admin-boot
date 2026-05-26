package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.enums.ResponseCode;
import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysConfig;
import com.admin.system.mapper.SysConfigMapper;
import com.admin.system.service.ISysConfigService;
import com.admin.system.dto.*;
import com.admin.system.query.SysConfigQuery;
import com.admin.system.vo.SysConfigVO;
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
 * 参数配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements ISysConfigService {

    private final SysConfigMapper configMapper;

    @Override
    public IPage<SysConfigVO> page(SysConfigQuery query) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getConfigName()), SysConfig::getConfigName, query.getConfigName())
               .like(StringUtils.isNotBlank(query.getConfigKey()), SysConfig::getConfigKey, query.getConfigKey())
               .eq(StringUtils.isNotBlank(query.getConfigType()), SysConfig::getConfigType, query.getConfigType())
               .orderByDesc(SysConfig::getCreateTime);

        IPage<SysConfig> page = configMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public SysConfigVO getById(Long id) {
        SysConfig config = configMapper.selectById(id);
        return config != null ? toVO(config) : null;
    }

    @Override
    @Cacheable(value = "sysConfig", key = "#configKey")
    public String getValueByKey(String configKey) {
        return configMapper.selectValueByKey(configKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void add(AddConfigDTO dto) {
        SysConfig config = new SysConfig();
        config.setConfigName(dto.getConfigName());
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        config.setConfigType(dto.getConfigType() != null ? dto.getConfigType() : "N");
        config.setRemark(dto.getRemark());
        configMapper.insert(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void update(UpdateConfigDTO dto) {
        SysConfig config = configMapper.selectById(dto.getId());
        if (config == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "配置不存在");
        }

        config.setConfigName(dto.getConfigName());
        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());
        if (dto.getConfigType() != null) {
            config.setConfigType(dto.getConfigType());
        }
        config.setRemark(dto.getRemark());
        configMapper.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void delete(Long id) {
        configMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "sysConfig", allEntries = true)
    public void deleteBatch(Long[] ids) {
        if (ids != null) {
            configMapper.deleteBatchIds(Arrays.asList(ids));
        }
    }

    @Override
    @Cacheable(value = "sysConfigList", key = "'all'")
    public List<SysConfigVO> listAll() {
        List<SysConfig> configs = configMapper.selectList(new LambdaQueryWrapper<>());
        return configs.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public boolean isConfigKeyUnique(String configKey) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey);
        return configMapper.selectCount(wrapper) == 0;
    }

    /**
     * 转换为VO
     */
    private SysConfigVO toVO(SysConfig config) {
        SysConfigVO vo = new SysConfigVO();
        vo.setId(config.getId());
        vo.setConfigName(config.getConfigName());
        vo.setConfigKey(config.getConfigKey());
        vo.setConfigValue(config.getConfigValue());
        vo.setConfigType(config.getConfigType());
        vo.setCreateTime(config.getCreateTime());
        vo.setRemark(config.getRemark());
        return vo;
    }
}
