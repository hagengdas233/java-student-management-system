package com.ljm.studentspringboot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "学生条件分页查询参数")
public class StudentQueryDTO {

    @Schema(description = "姓名关键字", example = "张")
    private String name;

    @Schema(description = "最低成绩", example = "60")
    @Min(value = 0, message = "最低成绩不能小于0")
    @Max(value = 100, message = "最低成绩不能大于100")
    private Integer minScore;

    @Schema(description = "最高成绩", example = "100")
    @Min(value = 0, message = "最高成绩不能小于0")
    @Max(value = 100, message = "最高成绩不能大于100")
    private Integer maxScore;

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    @Schema(description = "每页条数", example = "10")
    @Min(value = 1, message = "每页条数必须大于0")
    @Max(value = 100, message = "每页最多100条")
    private Integer pageSize = 10;
}