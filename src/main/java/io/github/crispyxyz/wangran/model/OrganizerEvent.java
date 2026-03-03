package io.github.crispyxyz.wangran.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 */
@TableName(value = "organizer_event")
@Data
public class OrganizerEvent {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private Integer organizerId;

    /**
     *
     */
    private Integer eventId;

    /**
     *
     */
    private Integer deleted;
}