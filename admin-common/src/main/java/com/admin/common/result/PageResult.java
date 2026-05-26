package com.admin.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应封装
 *
 * @param <T> 数据类型
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> records;
    private long total;
    private long size;
    private long current;
    private long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, long total, IPage<?> page) {
        this.records = records;
        this.total = total;
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
    }

    public static <T> PageResult<T> of(List<T> records, long total, IPage<?> page) {
        return new PageResult<>(records, total, page);
    }
}
