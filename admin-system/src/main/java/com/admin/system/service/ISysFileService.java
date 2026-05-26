package com.admin.system.service;

import com.admin.system.entity.SysFile;
import com.admin.system.vo.SysFileVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface ISysFileService extends IService<SysFile> {

    /**
     * 文件上传
     */
    SysFileVO upload(MultipartFile file);

    /**
     * 根据ID查询文件
     */
    SysFileVO getById(Long id);

    /**
     * 删除文件
     */
    void delete(Long id);

    /**
     * 下载文件
     */
    void download(Long id);
}
