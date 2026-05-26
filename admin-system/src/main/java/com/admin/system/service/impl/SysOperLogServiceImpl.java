package com.admin.system.service.impl;

import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysOperLog;
import com.admin.system.mapper.SysOperLogMapper;
import com.admin.system.service.ISysOperLogService;
import com.admin.system.query.SysOperLogQuery;
import com.admin.system.vo.SysOperLogVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 操作日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements ISysOperLogService {

    private final SysOperLogMapper operLogMapper;

    @Override
    public IPage<SysOperLogVO> page(SysOperLogQuery query) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getTitle()), SysOperLog::getTitle, query.getTitle())
               .eq(query.getBusinessType() != null, SysOperLog::getBusinessType, query.getBusinessType())
               .like(StringUtils.isNotBlank(query.getUserName()), SysOperLog::getUserName, query.getUserName())
               .eq(query.getOperStatus() != null, SysOperLog::getOperStatus, query.getOperStatus())
               .orderByDesc(SysOperLog::getOperTime);

        IPage<SysOperLog> page = operLogMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public SysOperLogVO getById(Long id) {
        SysOperLog operLog = operLogMapper.selectById(id);
        return operLog != null ? toVO(operLog) : null;
    }

    @Override
    public void delete(Long id) {
        operLogMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        if (ids != null) {
            operLogMapper.deleteBatchIds(Arrays.asList(ids));
        }
    }

    @Override
    public void clean() {
        operLogMapper.delete(new LambdaQueryWrapper<>());
    }

    @Override
    public boolean save(SysOperLog operLog) {
        operLogMapper.insert(operLog);
        return true;
    }

    /**
     * 转换为VO
     */
    private SysOperLogVO toVO(SysOperLog operLog) {
        SysOperLogVO vo = new SysOperLogVO();
        vo.setId(operLog.getId());
        vo.setTitle(operLog.getTitle());
        vo.setBusinessType(operLog.getBusinessType());
        vo.setMethod(operLog.getMethod());
        vo.setRequestMethod(operLog.getRequestMethod());
        vo.setUserId(operLog.getUserId());
        vo.setUserName(operLog.getUserName());
        vo.setUrl(operLog.getUrl());
        vo.setIpaddr(operLog.getIpaddr());
        vo.setOperLocation(operLog.getOperLocation());
        vo.setOperParam(operLog.getOperParam());
        vo.setJsonResult(operLog.getJsonResult());
        vo.setOperStatus(operLog.getOperStatus());
        vo.setErrorMsg(operLog.getErrorMsg());
        vo.setOperTime(operLog.getOperTime());
        vo.setCostTime(operLog.getCostTime());
        return vo;
    }
}
