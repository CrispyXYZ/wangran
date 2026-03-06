package io.github.crispyxyz.wangran.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "分页数据")
public class PageResponse<T> {
    @Schema(description = "数据列表")
    private List<T> data;
    @Schema(description = "数据总数", example = "100")
    private long total;
    @Schema(description = "页码总数", example = "10")
    private long pages;
    @Schema(description = "当前页码", example = "1")
    private long current;
    @Schema(description = "页码大小（每页数据数量）", example = "10")
    private long size;

    public PageResponse(IPage<T> pageInfo) {
        this.data = pageInfo.getRecords();
        this.total = pageInfo.getTotal();
        this.pages = pageInfo.getPages();
        this.current = pageInfo.getCurrent();
        this.size = pageInfo.getSize();
    }
}
