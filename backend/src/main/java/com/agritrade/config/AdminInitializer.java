package com.agritrade.config;

import com.agritrade.user.User;
import com.agritrade.user.UserMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Long count = userMapper.selectCount(Wrappers.<User>lambdaQuery().eq(User::getUsername, "admin"));
        if (count > 0) {
            return;
        }
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhone("13800000000");
        admin.setNickname("平台管理员");
        admin.setRole("ADMIN");
        admin.setStatus("ENABLED");
        userMapper.insert(admin);
    }
}
