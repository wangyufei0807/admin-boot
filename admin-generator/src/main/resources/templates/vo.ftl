package ${packageName}.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * ${entityName} 视图对象
 *
 * @author ${author}
 * @date ${date}
 */
@Schema(description = "${entityName} 视图对象")
@Data
public class ${entityName}VO {

<#if model.columnList??>
    <#list model.columnList as column>
    @Schema(description = "${column.comment!column.columnName}")
    private ${column.javaType} ${column.propertyName};
    </#list>
</#if>
}
