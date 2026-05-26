package ${packageName}.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ${entityName} 查询参数
 *
 * @author ${author}
 * @date ${date}
 */
@Schema(description = "${entityName} 查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class ${entityName}Query extends PageQuery {

<#if model.columnList??>
    <#list model.columnList as column>
    <#if column.columnName != 'id' && column.columnName != 'create_by' && column.columnName != 'create_time' && column.columnName != 'update_by' && column.columnName != 'update_time' && column.columnName != 'remark' && column.columnName != 'del_flag' && column.columnName != 'status' && column.columnName != 'sort' && column.columnName != 'order_by'>
    @Schema(description = "${column.comment!column.columnName}")
    private ${column.javaType} ${column.propertyName};
    </#if>
    </#list>
</#if>

    @Schema(description = "状态（0=禁用，1=正常）")
    private Integer status;
}
