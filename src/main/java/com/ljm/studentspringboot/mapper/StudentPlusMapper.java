package com.ljm.studentspringboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljm.studentspringboot.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentPlusMapper extends BaseMapper<Student> {
}
