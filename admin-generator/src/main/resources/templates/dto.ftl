package ${packageName}.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * ${entityName} 新增/修改 DTO
 *
 * @author ${author}
 * @date ${date}
 */
@Schema(description = "${entityName} 新增/修改 DTO")
@Data
public class ${entityName}DTO {

<#if model.columnList??>
    <#list model.columnList as column>
    <#if column.columnName != 'id' && column.columnName != 'create_by' && column.columnName != 'create_time' && column.columnName != 'update_by' && column.columnName != 'update_time' && column.columnName != 'remark' && column.columnName != 'del_flag'>
    @Schema(description = "${column.comment!column.columnName}")
    <#if column.isPk || !column.nullable>
    @NotNull(message = "${column.comment!column.columnName}不能为空")
    </#if>
    private ${column.javaType} ${column.propertyName};
    </#if>
    </#list>
</#if>
}
