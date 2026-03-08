package io.github.crispyxyz.wangran.mapper;

import com.github.yulichang.base.MPJBaseMapper;
import io.github.crispyxyz.wangran.model.UserEvent;
import org.apache.ibatis.annotations.Select;

/**
 *
 * 针对表【user_event】的数据库操作Mapper
 *
 *
 */
public interface UserEventMapper extends MPJBaseMapper<UserEvent> {
    @Select("select * from user_event where id = #{id} for update")
    UserEvent selectByIdForUpdate(Integer id);
}




