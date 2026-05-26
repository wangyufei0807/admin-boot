package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.dto.AddConfigDTO;
import com.admin.system.dto.UpdateConfigDTO;
import com.admin.system.query.SysConfigQuery;
import com.admin.system.service.ISysConfigService;
import com.admin.system.vo.SysConfigVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 参数配置控制器
 */
@Tag(name = "参数配置")
@RestController
@RequestMapping("/api/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService configService;

    @Operation(summary = "配置列表")
    @GetMapping("/list")
    @RequiresPermissions("system:config:list")
    public R<PageResult<SysConfigVO>> list(@ParameterObject SysConfigQuery query) {
        IPage<SysConfigVO> page = configService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "配置详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:config:query")
    public R<SysConfigVO> get(@Parameter(description = "配置ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(configService.getById(id));
    }

    @Operation(summary = "根据key获取配置")
    @GetMapping("/key/{configKey}")
    public R<String> getByKey(@Parameter(description = "配置key", required = true, example = "sys.index.skinName") @PathVariable String configKey) {
        return R.ok(configService.getValueByKey(configKey));
    }

    @Operation(summary = "获取所有配置")
    @GetMapping("/all")
    public R<List<SysConfigVO>> listAll() {
        return R.ok(configService.listAll());
    }

    @Operation(summary = "新增配置")
    @PostMapping
    @RequiresPermissions("system:config:add")
    @Log(title = "参数配置", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody @Valid AddConfigDTO dto) {
        configService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改配置")
    @PutMapping("/{id}")
    @RequiresPermissions("system:config:edit")
    @Log(title = "参数配置", businessType = BusinessType.UPDATE)
    public R<Void> update(@Parameter(description = "配置ID", required = true, example = "1") @PathVariable Long id,
                          @RequestBody @Valid UpdateConfigDTO dto) {
        dto.setId(id);
        configService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:config:remove")
    @Log(title = "参数配置", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "配置ID", required = true, example = "1") @PathVariable Long id) {
        configService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除配置")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("system:config:remove")
    @Log(title = "参数配置", businessType = BusinessType.DELETE)
    public R<Void> batchDelete(@Parameter(description = "配置ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        configService.deleteBatch(ids);
        return R.ok();
    }
}
