package com.ljm.studentspringboot.service;

import com.ljm.studentspringboot.dto.UserLoginDTO;
import com.ljm.studentspringboot.dto.UserRegisterDTO;
import com.ljm.studentspringboot.vo.UserLoginVO;

public interface UserService {

    void register(UserRegisterDTO userRegisterDTO);

    UserLoginVO login(UserLoginDTO userLoginDTO);
}
