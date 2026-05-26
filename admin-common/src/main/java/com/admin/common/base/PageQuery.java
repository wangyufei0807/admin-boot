package com.admin.common.base;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询基类
 */
@Schema(description = "分页查询参数")
@Data
public class PageQuery {

    @Schema(description = "当前页码", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "排序方式（asc/desc）", example = "desc")
    private String sort;

    /**
     * 转换为 MyBatis-Plus Page
     */
    public <T> Page<T> toPage() {
        return toPage(new Page<>());
    }

    /**
     * 转换为 MyBatis-Plus Page（指定类型）
     */
    public <T> Page<T> toPage(Page<T> page) {
        page.setCurrent(pageNum);
        page.setSize(pageSize);

        // 处理排序
        if (orderBy != null && !orderBy.isEmpty()) {
            List<OrderItem> orders = new ArrayList<>();
            boolean isAsc = !"desc".equalsIgnoreCase(sort);
            if (isAsc) {
                orders.add(OrderItem.asc(orderBy));
            } else {
                orders.add(OrderItem.desc(orderBy));
            }
            page.addOrder(orders);
        }

        return page;
    }

    /**
     * 获取 offset
     */
    public long getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
