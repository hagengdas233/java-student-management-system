package com.ljm.studentspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "添加学生请求参数")
public class StudentAddDTO {

    @Schema(description = "学号", example = "1001")
    @NotBlank(message = "学号不能为空")
    private String id;

    @Schema(description = "姓名", example = "张三")
    @NotBlank(message = "姓名不能为空")
    private String name;

    @Schema(description = "年龄", example = "18")
    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄必须大于0")
    private Integer age;

    @Schema(description = "成绩", example = "90")
    @NotNull(message = "成绩不能为空")
    @Min(value = 0, message = "成绩不能小于0")
    @Max(value = 100, message = "成绩不能大于100")
    private Integer score;
}