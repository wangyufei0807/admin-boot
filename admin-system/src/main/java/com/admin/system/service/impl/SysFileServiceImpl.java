package com.admin.system.service.impl;

import com.admin.common.exception.BusinessException;
import com.admin.common.enums.ResponseCode;
import com.admin.common.service.FileStorageService;
import com.admin.system.entity.SysFile;
import com.admin.system.mapper.SysFileMapper;
import com.admin.system.service.ISysFileService;
import com.admin.system.vo.SysFileVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile> implements ISysFileService {

    private final SysFileMapper fileMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SysFileVO upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "上传文件不能为空");
        }

        try {
            // 上传文件
            String filePath = fileStorageService.upload(file);

            // 保存文件记录
            SysFile sysFile = new SysFile();
            sysFile.setFileName(file.getOriginalFilename());
            sysFile.setFilePath(filePath);
            sysFile.setFileSize(file.getSize());
            sysFile.setFileType(file.getContentType());
            sysFile.setStorageType("local");
            fileMapper.insert(sysFile);

            return toVO(sysFile);
        } catch (Exception e) {
            log.error("File upload failed", e);
            throw new BusinessException(ResponseCode.FILE_ERROR, "文件上传失败");
        }
    }

    @Override
    public SysFileVO getById(Long id) {
        SysFile sysFile = fileMapper.selectById(id);
        return sysFile != null ? toVO(sysFile) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysFile sysFile = fileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "文件不存在");
        }

        // 删除物理文件
        fileStorageService.delete(sysFile.getFilePath());

        // 删除记录
        fileMapper.deleteById(id);
    }

    @Override
    public void download(Long id) {
        SysFile sysFile = fileMapper.selectById(id);
        if (sysFile == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST, "文件不存在");
        }

        fileStorageService.download(sysFile.getFilePath(), null);
    }

    /**
     * 转换为VO
     */
    private SysFileVO toVO(SysFile sysFile) {
        SysFileVO vo = new SysFileVO();
        vo.setId(sysFile.getId());
        vo.setFileName(sysFile.getFileName());
        vo.setFilePath(sysFile.getFilePath());
        vo.setFileSize(sysFile.getFileSize());
        vo.setFileType(sysFile.getFileType());
        vo.setStorageType(sysFile.getStorageType());
        vo.setCreateTime(sysFile.getCreateTime());
        vo.setUrl(fileStorageService.getUrl(sysFile.getFilePath()));
        return vo;
    }
}
