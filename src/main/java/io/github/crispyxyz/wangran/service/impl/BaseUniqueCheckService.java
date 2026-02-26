package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

public abstract class BaseUniqueCheckService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> {

    protected abstract SFunction<T, ?> getIdField();

    /**
     * 检查字段值是否冲突（排除自身）
     *
     * @param fieldGetter 字段 Lambda
     * @param value       字段值
     * @param currentId   当前 ID（可为 null）
     * @return true 冲突
     */
    protected boolean isFieldConflict(SFunction<T, ?> fieldGetter, Object value, Object currentId) {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(fieldGetter, value);
        if (currentId != null) {
            wrapper.ne(getIdField(), currentId);
        }
        return baseMapper.selectCount(wrapper) > 0;
    }
}
