package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.dto.AddRoleDTO;
import com.admin.system.dto.UpdateRoleDTO;
import com.admin.system.query.SysRoleQuery;
import com.admin.system.service.ISysRoleService;
import com.admin.system.vo.SysRoleVO;
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
 * 角色管理控制器
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    @Operation(summary = "角色列表")
    @GetMapping("/list")
    @RequiresPermissions("system:role:list")
    public R<PageResult<SysRoleVO>> list(@ParameterObject SysRoleQuery query) {
        IPage<SysRoleVO> page = roleService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "角色详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:role:query")
    public R<SysRoleVO> get(@Parameter(description = "角色ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(roleService.getById(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    @RequiresPermissions("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody @Valid AddRoleDTO dto) {
        roleService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public R<Void> update(@Parameter(description = "角色ID", required = true, example = "1") @PathVariable Long id,
                          @RequestBody @Valid UpdateRoleDTO dto) {
        dto.setId(id);
        roleService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "角色ID", required = true, example = "1") @PathVariable Long id) {
        roleService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除角色")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    public R<Void> batchDelete(@Parameter(description = "角色ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        roleService.deleteBatch(ids);
        return R.ok();
    }

    @Operation(summary = "修改角色状态")
    @PutMapping("/{id}/status")
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    public R<Void> updateStatus(@Parameter(description = "角色ID", required = true, example = "1") @PathVariable Long id,
                                @Parameter(description = "状态（0=禁用，1=正常）", required = true, example = "1") @RequestParam Integer status) {
        roleService.updateStatus(id, status);
        return R.ok();
    }

    @Operation(summary = "获取角色已分配菜单")
    @GetMapping("/{id}/menu")
    public R<List<Long>> getRoleMenu(@Parameter(description = "角色ID", required = true, example = "1") @PathVariable Long id) {
        SysRoleVO role = roleService.getById(id);
        return R.ok(role != null ? role.getMenuIds() : null);
    }

    @Operation(summary = "获取所有角色")
    @GetMapping("/all")
    public R<List<SysRoleVO>> listAll() {
        return R.ok(roleService.listAll());
    }
}
