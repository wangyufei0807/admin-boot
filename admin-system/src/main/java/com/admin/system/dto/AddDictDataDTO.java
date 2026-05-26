package com.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增字典数据请求参数
 */
@Schema(description = "新增字典数据请求参数")
@Data
public class AddDictDataDTO {

    @Schema(description = "字典排序")
    private Integer dictSort;

    @Schema(description = "字典标签", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    @Schema(description = "字典键值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典键值不能为空")
    private String dictValue;

    @Schema(description = "字典类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "样式属性")
    private String cssClass;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认（0=否，1=是）")
    private Integer isDefault;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}
