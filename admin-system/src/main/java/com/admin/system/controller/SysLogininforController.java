package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.query.SysLogininforQuery;
import com.admin.system.service.ISysLogininforService;
import com.admin.system.vo.SysLogininforVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 登录日志控制器
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/api/system/login-log")
@RequiredArgsConstructor
public class SysLogininforController {

    private final ISysLogininforService logininforService;

    @Operation(summary = "登录日志列表")
    @GetMapping("/list")
    @RequiresPermissions("monitor:logininfor:list")
    public R<PageResult<SysLogininforVO>> list(@ParameterObject SysLogininforQuery query) {
        IPage<SysLogininforVO> page = logininforService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "删除登录日志")
    @DeleteMapping("/{id}")
    @RequiresPermissions("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "访问ID", required = true, example = "1") @PathVariable Long id) {
        logininforService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除登录日志")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    public R<Void> batchDelete(@Parameter(description = "访问ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        logininforService.deleteBatch(ids);
        return R.ok();
    }

    @Operation(summary = "清空登录日志")
    @DeleteMapping("/clean")
    @RequiresPermissions("monitor:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.CLEAN)
    public R<Void> clean() {
        logininforService.clean();
        return R.ok();
    }
}
