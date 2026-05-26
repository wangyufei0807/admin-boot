package com.admin.common.utils;

import com.admin.common.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件验证工具类
 * 
 * 功能：
 * - MIME 类型验证
 * - 文件魔数验证
 * - 文件大小验证
 * - 扩展名验证
 * 
 * 改进说明：
 * - 从扩展名验证升级到 MIME + 魔数 + 大小 三层验证
 * - 防止文件伪装攻击
 * - 防止超大文件上传
 */
@Slf4j
public class FileValidationUtils {

    // 最大文件大小：10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    // 允许的 MIME 类型
    private static final Map<String, byte[][]> MIME_TYPE_SIGNATURES = new HashMap<>();
    
    static {
        // JPEG: FF D8 FF E0-EF
        MIME_TYPE_SIGNATURES.put("image/jpeg", new byte[][]{
            {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF}
        });
        
        // PNG: 89 50 4E 47
        MIME_TYPE_SIGNATURES.put("image/png", new byte[][]{
            {(byte) 0x89, 0x50, 0x4E, 0x47}
        });
        
        // GIF: 47 49 46 38
        MIME_TYPE_SIGNATURES.put("image/gif", new byte[][]{
            {0x47, 0x49, 0x46, 0x38}
        });
        
        // PDF: 25 50 44 46
        MIME_TYPE_SIGNATURES.put("application/pdf", new byte[][]{
            {0x25, 0x50, 0x44, 0x46}
        });
    }

    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp",
            "pdf",
            "doc", "docx",
            "xls", "xlsx"
    );

    private FileValidationUtils() {
    }

    /**
     * 验证文件合法性（综合验证）
     */
    public static void validateFile(MultipartFile file) throws FileUploadException {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("File cannot be empty");
        }

        // 1. 验证文件大小
        validateFileSize(file);

        // 2. 验证扩展名
        validateExtension(file.getOriginalFilename());

        // 3. 验证 MIME 类型
        validateMimeType(file.getContentType());

        // 4. 验证文件魔数（防止伪装）
        validateMagicNumber(file);
    }

    /**
     * 验证文件大小
     */
    private static void validateFileSize(MultipartFile file) throws FileUploadException {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException(
                    String.format("File size exceeds limit. Max: %s, Actual: %s",
                            FileUtils.getReadableFileSize(MAX_FILE_SIZE),
                            FileUtils.getReadableFileSize(file.getSize()))
            );
        }
    }

    /**
     * 验证文件扩展名
     */
    private static void validateExtension(String filename) throws FileUploadException {
        if (filename == null || filename.isEmpty()) {
            throw new FileUploadException("Filename cannot be empty");
        }

        String extension = FileUtils.getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileUploadException(
                    String.format("File type not allowed: %s. Allowed types: %s",
                            extension, ALLOWED_EXTENSIONS)
            );
        }
    }

    /**
     * 验证 MIME 类型
     */
    private static void validateMimeType(String contentType) throws FileUploadException {
        if (contentType == null || contentType.isEmpty()) {
            throw new FileUploadException("MIME type not detected");
        }

        // 检查是否在允许列表中
        boolean isAllowed = ALLOWED_MIME_TYPES.stream()
                .anyMatch(mime -> contentType.startsWith(mime));

        if (!isAllowed) {
            throw new FileUploadException(
                    String.format("MIME type not allowed: %s", contentType)
            );
        }
    }

    /**
     * 验证文件魔数（Magic Number/签名）
     * 防止用户改名后上传恶意文件
     */
    private static void validateMagicNumber(MultipartFile file) throws FileUploadException {
        byte[] bytes = new byte[16];
        try {
            int read = file.getInputStream().read(bytes);
            if (read < 4) {
                throw new FileUploadException("File is too small to validate");
            }
        } catch (IOException e) {
            throw new FileUploadException("Failed to read file content: " + e.getMessage(), e);
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new FileUploadException("Cannot determine file type");
        }

        // 获取该 MIME 类型对应的魔数
        byte[][] signatures = MIME_TYPE_SIGNATURES.get(contentType);
        if (signatures == null) {
            // 如果没有定义魔数检查，则跳过（但 MIME 已验证）
            log.debug("No magic number check defined for MIME type: {}", contentType);
            return;
        }

        // 检查文件是否匹配任意一个签名
        boolean matched = false;
        for (byte[] signature : signatures) {
            if (matchesSignature(bytes, signature)) {
                matched = true;
                break;
            }
        }

        if (!matched) {
            throw new FileUploadException(
                    String.format("File content does not match MIME type: %s. " +
                                    "The file may be disguised or corrupted.",
                            contentType)
            );
        }
    }

    /**
     * 检查字节数组是否与签名匹配
     */
    private static boolean matchesSignature(byte[] data, byte[] signature) {
        if (data.length < signature.length) {
            return false;
        }

        for (int i = 0; i < signature.length; i++) {
            if (data[i] != signature[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查是否为图片
     */
    public static boolean isImage(String filename) {
        String extension = FileUtils.getFileExtension(filename).toLowerCase();
        return Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(extension);
    }

    /**
     * 检查是否为文档
     */
    public static boolean isDocument(String filename) {
        String extension = FileUtils.getFileExtension(filename).toLowerCase();
        return Arrays.asList("pdf", "doc", "docx", "xls", "xlsx").contains(extension);
    }
}
