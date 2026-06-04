package com.agritrade.auth;

import cn.dev33.satoken.stp.StpUtil;
import com.agritrade.common.BizException;
import com.agritrade.user.User;
import com.agritrade.user.UserMapper;
import com.agritrade.user.UserRole;
import com.agritrade.user.UserStatus;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, request.getUsername())) > 0) {
            throw new BizException("用户名已存在");
        }
        if (userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getPhone, request.getPhone())) > 0) {
            throw new BizException("手机号已存在");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setNickname(request.getNickname() == null ? request.getUsername() : request.getNickname());
        user.setRole(UserRole.CONSUMER.name());
        user.setStatus(UserStatus.ENABLED.name());
        userMapper.insert(user);
        user.setPassword(null);
        return user;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, request.getUsername()));
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BizException("用户名或密码错误");
        }
        if (!UserStatus.ENABLED.name().equals(user.getStatus())) {
            throw new BizException("账号已禁用");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
        StpUtil.login(user.getId());
        user.setPassword(null);
        return new LoginResponse(StpUtil.getTokenValue(), user);
    }

    public User currentUser() {
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        if (user == null) {
            throw new BizException("用户不存在");
        }
        user.setPassword(null);
        return user;
    }

    public void requireRole(String role) {
        User user = currentUser();
        if (!role.equals(user.getRole())) {
            throw new BizException("无权限操作");
        }
    }
}
