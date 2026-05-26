package com.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件表实体
 */
@Schema(description = "文件表实体")
@Data
@TableName("sys_file")
public class SysFile implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件路径")
    private String filePath;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型（MIME）")
    private String fileType;

    @Schema(description = "存储类型（local/oss/s3）")
    private String storageType;

    @Schema(description = "创建者")
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private String createBy;

    @Schema(description = "创建时间")
    @TableField(fill = com.baomidou.mybatisplus.annotation.FieldFill.INSERT)
    private LocalDateTime createTime;
}
