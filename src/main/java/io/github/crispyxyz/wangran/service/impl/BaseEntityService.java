package io.github.crispyxyz.wangran.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.crispyxyz.wangran.exception.ResourceConflictException;
import io.github.crispyxyz.wangran.exception.ResourceNotFoundException;
import io.github.crispyxyz.wangran.service.EntityService;
import io.github.crispyxyz.wangran.util.SecurityUtil;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
// TODO 引入通用查重方法？

/**
 * 实体业务逻辑的抽象基类。用于封装便捷方法从而降低代码重复率
 *
 * @param <M> 实体类对应的 Mapper，应继承 BaseMapper
 * @param <T> 实体类
 */
public abstract class BaseEntityService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T>
    implements EntityService<T> {

    protected abstract SFunction<T, ?> getIdField();

    /**
     * 通用分页查询（无查询条件）
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<T> getPage(int page, int pageSize) {
        return page(Page.of(page, pageSize));
    }

    /**
     * 支持条件构造器的分页查询（可被子类复用或直接调用）
     */
    @Transactional(readOnly = true)
    @Override
    public IPage<T> getPage(int page, int pageSize, Wrapper<T> queryWrapper) {
        return page(Page.of(page, pageSize), queryWrapper);
    }

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

    /**
     * 创建一个 UpdateBuilder，可按需更新该 Service 对应的实体类的数据库字段
     *
     * @param id 主键id
     * @return UpdateBuilder
     */
    protected UpdateBuilder updateBuilder(Serializable id) {
        return new UpdateBuilder(id);
    }

    public class UpdateBuilder {
        private final Serializable id;
        private final LambdaUpdateWrapper<T> wrapper;

        private UpdateBuilder(Serializable id) {
            this.id = id;
            this.wrapper = Wrappers.lambdaUpdate(BaseEntityService.this.getEntityClass())
                                   .eq(getIdField(), id);
        }

        /**
         * 设置一个需要修改的字段，并检查唯一性冲突
         *
         * @param getter          字段对应的 getter 方法
         * @param value           字段的新值
         * @param conflictMessage 冲突后抛出异常的信息
         * @return UpdateBuilder自身
         */
        public UpdateBuilder setUnique(SFunction<T, ?> getter, Object value, String conflictMessage) {
            if (value != null) {
                if (isFieldConflict(getter, value, id)) {
                    throw new ResourceConflictException(conflictMessage);
                }
                wrapper.set(getter, value);
            }
            return this;
        }

        /**
         * 设置一个需要修改的字段，不检查冲突
         *
         * @param getter 字段对应的 getter 方法
         * @param value  字段的新值
         * @return UpdateBuilder自身
         */
        public UpdateBuilder set(SFunction<T, ?> getter, Object value) {
            if (value != null) {
                wrapper.set(getter, value);
            }
            return this;
        }

        /**
         * 设置密码字段，不检查冲突
         *
         * @param getter        字段对应的 getter 方法
         * @param plainPassword 原始密码字符串
         * @return UpdateBuilder自身
         */
        public UpdateBuilder setPassword(SFunction<T, byte[]> getter, String plainPassword) {
            if (plainPassword != null) {
                byte[] encrypted = SecurityUtil.computeSha256(plainPassword);
                wrapper.set(getter, encrypted);
            }
            return this;
        }

        /**
         * 执行更新并返回更新后的实体
         *
         * @return 更新后的实体对象
         */
        public T execute() {
            if (!update(wrapper)) {
                throw new ResourceNotFoundException("实体不存在");
            }
            return getById(id);
        }
    }
}
