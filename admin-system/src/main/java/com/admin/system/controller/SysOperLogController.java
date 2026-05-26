package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.query.SysOperLogQuery;
import com.admin.system.service.ISysOperLogService;
import com.admin.system.vo.SysOperLogVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志控制器
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/system/oper-log")
@RequiredArgsConstructor
public class SysOperLogController {

    private final ISysOperLogService operLogService;

    @Operation(summary = "操作日志列表")
    @GetMapping("/list")
    @RequiresPermissions("monitor:operlog:list")
    public R<PageResult<SysOperLogVO>> list(@ParameterObject SysOperLogQuery query) {
        IPage<SysOperLogVO> page = operLogService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "操作日志详情")
    @GetMapping("/{id}")
    @RequiresPermissions("monitor:operlog:query")
    public R<SysOperLogVO> get(@Parameter(description = "日志主键", required = true, example = "1") @PathVariable Long id) {
        return R.ok(operLogService.getById(id));
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    @RequiresPermissions("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "日志主键", required = true, example = "1") @PathVariable Long id) {
        operLogService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    public R<Void> batchDelete(@Parameter(description = "日志ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        operLogService.deleteBatch(ids);
        return R.ok();
    }

    @Operation(summary = "清空操作日志")
    @DeleteMapping("/clean")
    @RequiresPermissions("monitor:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    public R<Void> clean() {
        operLogService.clean();
        return R.ok();
    }
}
