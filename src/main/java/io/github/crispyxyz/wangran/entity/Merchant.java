package io.github.crispyxyz.wangran.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("merchants")
@Data
public class Merchant {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String merchantId;

    private String phoneNumber;

    /**
     * 0=审核中 1=审核通过 2=审核不通过
     */
    private Integer approvalStatus;

    private String rejectReason;

    private byte[] passwordSha256;
}