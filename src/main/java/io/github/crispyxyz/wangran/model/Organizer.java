package io.github.crispyxyz.wangran.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@TableName(value = "organizer")
@Data
public class Organizer {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String phoneNumber;

    /**
     *
     */
    private String address;

    /**
     *
     */
    private Integer deleted;

    @TableField(exist = false)
    private List<Event> events = new ArrayList<>();
}