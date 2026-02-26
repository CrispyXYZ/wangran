package io.github.crispyxyz.wangran.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 */
@TableName(value = "event_table")
@Data
public class Event {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String eventCode;

    /**
     *
     */
    private String eventName;

    /**
     *
     */
    private String eventType;

    /**
     *
     */
    private Date eventTime;

    /**
     *
     */
    private String city;

    /**
     *
     */
    private BigDecimal price;

    /**
     *
     */
    private Integer stock;

    /**
     *
     */
    private Integer onShelf;

    /**
     *
     */
    private Date saleStartTime;

    /**
     *
     */
    private Date saleEndTime;

    /**
     *
     */
    private Integer merchantId;

    /**
     *
     */
    private Integer deleted;

    @TableField(exist = false)
    private List<Organizer> organizers;

    @TableField(exist = false)
    private List<UserEvent> userEvents;
}