package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.R;
import com.admin.system.service.ISysFileService;
import com.admin.system.vo.SysFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/system/file")
@RequiredArgsConstructor
public class SysFileController {

    private final ISysFileService fileService;

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    @RequiresPermissions("system:file:upload")
    @Log(title = "文件管理", businessType = BusinessType.INSERT)
    public R<SysFileVO> upload(@Parameter(description = "上传文件", required = true) @RequestParam("file") MultipartFile file) {
        return R.ok(fileService.upload(file));
    }

    @Operation(summary = "文件详情")
    @GetMapping("/{id}")
    public R<SysFileVO> get(@Parameter(description = "文件ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(fileService.getById(id));
    }

    @Operation(summary = "文件下载")
    @GetMapping("/{id}/download")
    public void download(@Parameter(description = "文件ID", required = true, example = "1") @PathVariable Long id,
                         HttpServletResponse response) {
        fileService.download(id);
    }

    @Operation(summary = "删除文件")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:file:remove")
    @Log(title = "文件管理", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "文件ID", required = true, example = "1") @PathVariable Long id) {
        fileService.delete(id);
        return R.ok();
    }
}
