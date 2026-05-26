package com.admin.system.service.impl;

import com.admin.common.utils.StringUtils;
import com.admin.system.entity.SysLogininfor;
import com.admin.system.mapper.SysLogininforMapper;
import com.admin.system.service.ISysLogininforService;
import com.admin.system.query.SysLogininforQuery;
import com.admin.system.vo.SysLogininforVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 系统访问记录服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLogininforServiceImpl extends ServiceImpl<SysLogininforMapper, SysLogininfor> implements ISysLogininforService {

    private final SysLogininforMapper logininforMapper;

    @Override
    public IPage<SysLogininforVO> page(SysLogininforQuery query) {
        LambdaQueryWrapper<SysLogininfor> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getUserName()), SysLogininfor::getUserName, query.getUserName())
               .eq(query.getLoginStatus() != null, SysLogininfor::getLoginStatus, query.getLoginStatus())
               .orderByDesc(SysLogininfor::getLoginTime);

        IPage<SysLogininfor> page = logininforMapper.selectPage(query.toPage(), wrapper);
        return page.convert(this::toVO);
    }

    @Override
    public void delete(Long id) {
        logininforMapper.deleteById(id);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        if (ids != null) {
            logininforMapper.deleteBatchIds(Arrays.asList(ids));
        }
    }

    @Override
    public void clean() {
        logininforMapper.delete(new LambdaQueryWrapper<>());
    }

    @Override
    public void recordLogininfor(Long userId, String username, String ipaddr, String msg, Integer loginStatus) {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserId(userId);
        logininfor.setUserName(username);
        logininfor.setIpaddr(ipaddr);
        logininfor.setLoginStatus(loginStatus);
        logininfor.setMsg(msg);
        logininfor.setLoginTime(LocalDateTime.now());
        logininforMapper.insert(logininfor);
    }

    /**
     * 转换为VO
     */
    private SysLogininforVO toVO(SysLogininfor logininfor) {
        SysLogininforVO vo = new SysLogininforVO();
        vo.setId(logininfor.getId());
        vo.setUserId(logininfor.getUserId());
        vo.setUserName(logininfor.getUserName());
        vo.setIpaddr(logininfor.getIpaddr());
        vo.setLoginLocation(logininfor.getLoginLocation());
        vo.setBrowser(logininfor.getBrowser());
        vo.setOs(logininfor.getOs());
        vo.setLoginStatus(logininfor.getLoginStatus());
        vo.setMsg(logininfor.getMsg());
        vo.setLoginTime(logininfor.getLoginTime());
        return vo;
    }
}
