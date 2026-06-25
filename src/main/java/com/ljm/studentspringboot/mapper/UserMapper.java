package com.ljm.studentspringboot.mapper;

import com.ljm.studentspringboot.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User findByUsername(String username);

    int insert(User user);
}
