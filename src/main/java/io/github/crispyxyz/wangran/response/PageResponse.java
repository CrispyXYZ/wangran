package io.github.crispyxyz.wangran.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> data;
    private long total;
    private long pages;
    private long current;
    private long size;

    public PageResponse(IPage<T> pageInfo) {
        this.data = pageInfo.getRecords();
        this.total = pageInfo.getTotal();
        this.pages = pageInfo.getPages();
        this.current = pageInfo.getCurrent();
        this.size = pageInfo.getSize();
    }
}
