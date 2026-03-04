package io.github.crispyxyz.wangran.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.Instant;

/**
 *
 */
@TableName(value = "user_event")
@Data
public class UserEvent {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String ticketCode;

    /**
     *
     */
    private Instant createTime;

    /**
     *
     */
    private Integer refunded;

    /**
     *
     */
    private Integer userId;

    /**
     *
     */
    private Integer eventId;

    /**
     *
     */
    private Integer deleted;

    @TableField(exist = false)
    private Event eventObject;
}