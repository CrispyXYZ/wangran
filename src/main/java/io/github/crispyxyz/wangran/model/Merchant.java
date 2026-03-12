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
@TableName(value = "merchant")
@Data
public class Merchant {
    public static final int STATUS_PENDING = 0;
    public static final int STATUS_APPROVED = 1;
    public static final int STATUS_REJECTED = 2;
    public static final String USERNAME_PREFIX = "merchant_";
    public static final String CODE_PREFIX = "mid_";

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    private String username;

    /**
     *
     */
    private String merchantCode;

    /**
     *
     */
    private String phoneNumber;

    /**
     * 0=审核中 1=审核通过 2=审核不通过
     */
    private Integer approvalStatus;

    /**
     *
     */
    private String rejectReason;

    /**
     *
     */
    private Integer deleted;

    /**
     *
     */
    private byte[] passwordSha256;

    @TableField(exist = false)
    private List<Event> events = new ArrayList<>();
}