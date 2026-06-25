package com.ljm.studentspringboot.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "学生返回数据")
public class StudentVO {

    @Schema(description = "学号", example = "1001")
    private String id;

    @Schema(description = "姓名", example = "张三")
    private String name;

    @Schema(description = "年龄", example = "18")
    private Integer age;

    @Schema(description = "成绩", example = "90")
    private Integer score;
}