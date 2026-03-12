package io.github.crispyxyz.wangran.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 */
@TableName(value = "user_table")
@Data
public class User {
    public static final String USERNAME_PREFIX = "user_";
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
    private String phoneNumber;

    /**
     *
     */
    private Integer deleted;

    /**
     *
     */
    private byte[] passwordSha256;
}