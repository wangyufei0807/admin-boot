package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.dto.AddDictDTO;
import com.admin.system.dto.AddDictDataDTO;
import com.admin.system.dto.UpdateDictDTO;
import com.admin.system.dto.UpdateDictDataDTO;
import com.admin.system.query.SysDictDataQuery;
import com.admin.system.query.SysDictQuery;
import com.admin.system.service.ISysDictDataService;
import com.admin.system.service.ISysDictService;
import com.admin.system.vo.SysDictDataVO;
import com.admin.system.vo.SysDictVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 */
@Tag(name = "字典管理")
@RestController
@RequestMapping("/api/system/dict")
@RequiredArgsConstructor
public class SysDictController {

    private final ISysDictService dictService;
    private final ISysDictDataService dictDataService;

    // ==================== 字典类型 ====================

    @Operation(summary = "字典类型列表")
    @GetMapping("/type/list")
    @RequiresPermissions("system:dict:list")
    public R<PageResult<SysDictVO>> typeList(@ParameterObject SysDictQuery query) {
        IPage<SysDictVO> page = dictService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "字典类型详情")
    @GetMapping("/type/{id}")
    @RequiresPermissions("system:dict:query")
    public R<SysDictVO> typeGet(@Parameter(description = "字典ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(dictService.getById(id));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping("/type")
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典管理", businessType = BusinessType.INSERT)
    public R<Void> typeAdd(@RequestBody @Valid AddDictDTO dto) {
        dictService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/type/{id}")
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典管理", businessType = BusinessType.UPDATE)
    public R<Void> typeUpdate(@Parameter(description = "字典ID", required = true, example = "1") @PathVariable Long id,
                              @RequestBody @Valid UpdateDictDTO dto) {
        dto.setId(id);
        dictService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/type/{id}")
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    public R<Void> typeDelete(@Parameter(description = "字典ID", required = true, example = "1") @PathVariable Long id) {
        dictService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除字典类型")
    @DeleteMapping("/type/batch/{ids}")
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    public R<Void> typeBatchDelete(@Parameter(description = "字典ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        dictService.deleteBatch(ids);
        return R.ok();
    }

    // ==================== 字典数据 ====================

    @Operation(summary = "字典数据列表")
    @GetMapping("/data/list")
    @RequiresPermissions("system:dict:list")
    public R<PageResult<SysDictDataVO>> dataList(@ParameterObject SysDictDataQuery query) {
        IPage<SysDictDataVO> page = dictDataService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "字典数据详情")
    @GetMapping("/data/{id}")
    @RequiresPermissions("system:dict:query")
    public R<SysDictDataVO> dataGet(@Parameter(description = "字典数据ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(dictDataService.getById(id));
    }

    @Operation(summary = "获取字典数据")
    @GetMapping("/data/{dictType}")
    public R<List<SysDictDataVO>> getByDictType(@Parameter(description = "字典类型", required = true, example = "sys_user_sex") @PathVariable String dictType) {
        return R.ok(dictDataService.getByDictType(dictType));
    }

    @Operation(summary = "新增字典数据")
    @PostMapping("/data")
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典管理", businessType = BusinessType.INSERT)
    public R<Void> dataAdd(@RequestBody @Valid AddDictDataDTO dto) {
        dictDataService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改字典数据")
    @PutMapping("/data/{id}")
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典管理", businessType = BusinessType.UPDATE)
    public R<Void> dataUpdate(@Parameter(description = "字典数据ID", required = true, example = "1") @PathVariable Long id,
                              @RequestBody @Valid UpdateDictDataDTO dto) {
        dto.setId(id);
        dictDataService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除字典数据")
    @DeleteMapping("/data/{id}")
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    public R<Void> dataDelete(@Parameter(description = "字典数据ID", required = true, example = "1") @PathVariable Long id) {
        dictDataService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除字典数据")
    @DeleteMapping("/data/batch/{ids}")
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典管理", businessType = BusinessType.DELETE)
    public R<Void> dataBatchDelete(@Parameter(description = "字典数据ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        dictDataService.deleteBatch(ids);
        return R.ok();
    }
}
