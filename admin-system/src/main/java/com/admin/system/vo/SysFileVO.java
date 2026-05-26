package com.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件视图对象
 */
@Schema(description = "文件视图对象")
@Data
public class SysFileVO {

    @Schema(description = "文件ID", example = "1")
    private Long id;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型（MIME）")
    private String fileType;

    @Schema(description = "存储类型")
    private String storageType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "文件URL")
    private String url;
}
