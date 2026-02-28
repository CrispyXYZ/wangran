package io.github.crispyxyz.wangran.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface EntityService<T> extends IService<T> {
    IPage<T> getPage(int page, int pageSize);

    IPage<T> getPage(int page, int pageSize, Wrapper<T> queryWrapper);
}
