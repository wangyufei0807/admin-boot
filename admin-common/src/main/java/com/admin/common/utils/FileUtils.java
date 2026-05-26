package com.admin.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件工具类
 */
public class FileUtils {

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "bmp", "webp"
    );

    private static final List<String> ALLOWED_DOCUMENT_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
    );

    private static final List<String> ALLOWED_ARCHIVE_EXTENSIONS = Arrays.asList(
            "zip", "rar", "7z", "tar", "gz"
    );

    private FileUtils() {
    }

    /**
     * 生成随机文件名
     */
    public static String generateFileName(String originalFilename) {
        if (originalFilename == null || originalFilename.isEmpty()) {
            return UUID.randomUUID().toString();
        }
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return extension.isEmpty() ? uuid : uuid + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取不带扩展名的文件名
     */
    public static String getFileNameWithoutExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return filename;
        }
        return filename.substring(0, lastDotIndex);
    }

    /**
     * 检查文件是否为图片
     */
    public static boolean isImage(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 检查文件是否为文档
     */
    public static boolean isDocument(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_DOCUMENT_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 检查文件是否为压缩包
     */
    public static boolean isArchive(String filename) {
        String extension = getFileExtension(filename);
        return ALLOWED_ARCHIVE_EXTENSIONS.contains(extension.toLowerCase());
    }

    /**
     * 创建目录（如果不存在）
     */
    public static void createDirectoryIfNotExists(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    /**
     * 删除文件
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * 获取文件大小（字节转人类可读）
     */
    public static String getReadableFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}
