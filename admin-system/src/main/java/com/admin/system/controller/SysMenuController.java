package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.dto.AddMenuDTO;
import com.admin.system.dto.UpdateMenuDTO;
import com.admin.system.query.SysMenuQuery;
import com.admin.system.service.ISysMenuService;
import com.admin.system.vo.SysMenuVO;
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
 * 菜单管理控制器
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final ISysMenuService menuService;

    @Operation(summary = "菜单列表")
    @GetMapping("/list")
    @RequiresPermissions("system:menu:list")
    public R<PageResult<SysMenuVO>> list(@ParameterObject SysMenuQuery query) {
        IPage<SysMenuVO> page = menuService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "菜单详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:menu:query")
    public R<SysMenuVO> get(@Parameter(description = "菜单ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody @Valid AddMenuDTO dto) {
        menuService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改菜单")
    @PutMapping("/{id}")
    @RequiresPermissions("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    public R<Void> update(@Parameter(description = "菜单ID", required = true, example = "1") @PathVariable Long id,
                          @RequestBody @Valid UpdateMenuDTO dto) {
        dto.setId(id);
        menuService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "菜单ID", required = true, example = "1") @PathVariable Long id) {
        menuService.delete(id);
        return R.ok();
    }

    @Operation(summary = "菜单下拉树")
    @GetMapping("/treeselect")
    public R<List<SysMenuVO>> treeselect() {
        return R.ok(menuService.treeselect());
    }

    @Operation(summary = "根据角色获取菜单下拉树")
    @GetMapping("/role/{roleId}/treeselect")
    public R<List<SysMenuVO>> roleMenuTreeselect(@Parameter(description = "角色ID", required = true, example = "1") @PathVariable Long roleId) {
        return R.ok(menuService.getRoleMenuTreeselect(roleId));
    }
}
