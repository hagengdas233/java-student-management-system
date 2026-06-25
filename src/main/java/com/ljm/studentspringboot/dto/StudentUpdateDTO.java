package com.ljm.studentspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "修改学生请求参数")
public class StudentUpdateDTO {

    @Schema(description = "姓名", example = "李四")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "年龄", example = "20")
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    private Integer age;

    @Schema(description = "成绩", example = "88")
    @NotNull(message = "成绩不能为空")
    @Min(value = 0, message = "成绩不能小于0")
    @Max(value = 100, message = "成绩不能大于100")
    private Integer score;
}