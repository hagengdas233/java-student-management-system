package com.ljm.studentspringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("student")
public class Student {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;
    private String name;
    private Integer age;
    private Integer score;
    @TableField("create_user_id")
    private Long createUserId;
    @TableField("create_username")
    private String createUsername;
}
