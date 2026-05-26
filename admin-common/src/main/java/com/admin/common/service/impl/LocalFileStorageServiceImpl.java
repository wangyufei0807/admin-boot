package com.admin.common.service.impl;

import com.admin.common.exception.FileUploadException;
import com.admin.common.service.FileStorageService;
import com.admin.common.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地文件存储服务实现
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "file.storage-type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-path:/data/upload}")
    private String uploadPath;

    @Override
    public String upload(MultipartFile file) {
        return upload(file, null);
    }

    @Override
    public String upload(MultipartFile file, String path) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("上传文件不能为空");
        }

        try {
            // 创建上传目录
            String basePath = uploadPath;
            if (path != null && !path.isEmpty()) {
                basePath = basePath + File.separator + path;
            }
            FileUtils.createDirectoryIfNotExists(basePath);

            // 生成文件名
            String fileName = FileUtils.generateFileName(file.getOriginalFilename());
            String filePath = basePath + File.separator + fileName;

            // 保存文件
            File dest = new File(filePath);
            file.transferTo(dest);

            // 返回相对路径
            String relativePath = path != null ? path + "/" + fileName : fileName;
            log.info("File uploaded successfully: {}", relativePath);

            return relativePath;
        } catch (IOException e) {
            log.error("File upload failed", e);
            throw new FileUploadException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public void delete(String path) {
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String filePath = uploadPath + File.separator + path.replace("/", File.separator);
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("File deleted successfully: {}", path);
                }
            }
        } catch (Exception e) {
            log.error("File delete failed: {}", path, e);
        }
    }

    @Override
    public String getUrl(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        // 本地存储返回相对路径，前端自行拼接域名
        return "/api/system/file/" + path;
    }

    @Override
    public void download(String path, HttpServletResponse response) {
        if (path == null || path.isEmpty()) {
            throw new FileUploadException("文件路径不能为空");
        }

        try {
            String filePath = uploadPath + File.separator + path.replace("/", File.separator);
            File file = new File(filePath);

            if (!file.exists()) {
                throw new FileUploadException("文件不存在");
            }

            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(file.length());
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + new String(file.getName().getBytes("UTF-8"), "ISO-8859-1") + "\"");

            // 写入响应流
            try (FileInputStream fis = new FileInputStream(file);
                 OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }
        } catch (IOException e) {
            log.error("File download failed: {}", path, e);
            throw new FileUploadException("文件下载失败");
        }
    }

    @Override
    public boolean exists(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        try {
            String filePath = uploadPath + File.separator + path.replace("/", File.separator);
            return Files.exists(Paths.get(filePath));
        } catch (Exception e) {
            return false;
        }
    }
}
