package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.annotation.RequiresPermissions;
import com.admin.common.enums.BusinessType;
import com.admin.common.result.PageResult;
import com.admin.common.result.R;
import com.admin.system.dto.*;
import com.admin.system.query.SysUserQuery;
import com.admin.system.service.ISysUserService;
import com.admin.system.vo.SysUserVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import org.springdoc.core.annotations.ParameterObject;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final ISysUserService userService;

    @Operation(summary = "用户列表")
    @GetMapping("/list")
    @RequiresPermissions("system:user:list")
    public R<PageResult<SysUserVO>> list(@ParameterObject SysUserQuery query) {
        IPage<SysUserVO> page = userService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @RequiresPermissions("system:user:query")
    public R<SysUserVO> get(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody @Valid AddUserDTO dto) {
        userService.add(dto);
        return R.ok();
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public R<Void> update(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id,
                          @RequestBody @Valid UpdateUserDTO dto) {
        dto.setId(id);
        userService.update(dto);
        return R.ok();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public R<Void> delete(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }

    @Operation(summary = "批量删除用户")
    @DeleteMapping("/batch/{ids}")
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public R<Void> batchDelete(@Parameter(description = "用户ID列表", required = true, example = "1,2,3") @PathVariable Long[] ids) {
        userService.deleteBatch(ids);
        return R.ok();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/{id}/password")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public R<Void> updatePassword(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id,
                                  @RequestBody @Valid UpdatePasswordDTO dto) {
        dto.setId(id);
        userService.updatePassword(dto);
        return R.ok();
    }

    @Operation(summary = "修改头像")
    @PutMapping("/{id}/avatar")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public R<Void> updateAvatar(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id,
                                @RequestBody UpdateAvatarDTO dto) {
        dto.setId(id);
        userService.updateAvatar(dto);
        return R.ok();
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/{id}/profile")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public R<Void> updateProfile(@Parameter(description = "用户ID", required = true, example = "1") @PathVariable Long id,
                                 @RequestBody @Valid UpdateProfileDTO dto) {
        dto.setId(id);
        userService.updateProfile(dto);
        return R.ok();
    }

    @Operation(summary = "获取所有用户")
    @GetMapping("/all")
    public R<List<SysUserVO>> listAll() {
        return R.ok(userService.list().stream()
                .map(user -> {
                    SysUserVO vo = new SysUserVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setNickName(user.getNickName());
                    vo.setEmail(user.getEmail());
                    vo.setPhone(user.getPhone());
                    vo.setAvatar(user.getAvatar());
                    vo.setSex(user.getSex());
                    vo.setStatus(user.getStatus());
                    vo.setCreateTime(user.getCreateTime());
                    return vo;
                })
                .collect(Collectors.toList()));
    }
}
