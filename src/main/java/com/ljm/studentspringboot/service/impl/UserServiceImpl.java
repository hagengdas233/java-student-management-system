package com.ljm.studentspringboot.service.impl;

import com.ljm.studentspringboot.dto.UserLoginDTO;
import com.ljm.studentspringboot.dto.UserRegisterDTO;
import com.ljm.studentspringboot.entity.User;
import com.ljm.studentspringboot.exception.BusinessException;
import com.ljm.studentspringboot.mapper.UserMapper;
import com.ljm.studentspringboot.service.UserService;
import com.ljm.studentspringboot.util.JwtUtil;
import com.ljm.studentspringboot.vo.UserLoginVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void register(UserRegisterDTO userRegisterDTO) {
        User existUser = userMapper.findByUsername(userRegisterDTO.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        int rows = userMapper.insert(user);
        if (rows <= 0) {
            throw new BusinessException("注册失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        User user = userMapper.findByUsername(userLoginDTO.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        boolean passwordMatches = passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword());
        if (!passwordMatches) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());
        return new UserLoginVO(user.getId(), user.getUsername(), token);
    }
}
