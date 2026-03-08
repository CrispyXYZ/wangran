package io.github.crispyxyz.wangran.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import io.github.crispyxyz.wangran.model.Event;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 *
 * 针对表【event_table】的数据库操作Mapper
 *
 *
 */
public interface EventMapper extends MPJBaseMapper<Event> {

    /**
     * 查询时加悲观锁
     */
    @Select("select * from event_table where id = #{id} for update")
    Event selectByIdForUpdate(Integer id);

    /**
     * 原子操作，防超卖
     */
    @Update("update event_table set stock = stock - 1 where id = #{id} and stock > 0")
    int updateStockDecreaseById(Integer id);

    /**
     * 原子操作
     */
    @Update("update event_table set stock = stock + 1 where id = #{id}")
    int updateStockIncreaseById(Integer id);

}




