package com.admin.common.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 文件存储路径
     */
    String upload(MultipartFile file);

    /**
     * 上传文件（指定路径）
     *
     * @param file 上传的文件
     * @param path 指定路径
     * @return 文件存储路径
     */
    String upload(MultipartFile file, String path);

    /**
     * 删除文件
     *
     * @param path 文件路径
     */
    void delete(String path);

    /**
     * 获取文件访问URL
     *
     * @param path 文件路径
     * @return 文件访问URL
     */
    String getUrl(String path);

    /**
     * 下载文件
     *
     * @param path     文件路径
     * @param response HTTP响应
     */
    void download(String path, HttpServletResponse response);

    /**
     * 检查文件是否存在
     *
     * @param path 文件路径
     * @return 是否存在
     */
    boolean exists(String path);
}
