package com.sky.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 定义 Argon2 加密器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(16, 32, 1, 4096, 3);
    }

    // 2. 核心配置：放行所有请求
    // 因为苍穹外卖项目已经有自己的拦截器(LoginCheckInterceptor)做权限校验了
    // 所以这里我们需要关闭 Spring Security 的默认拦截，否则两者会冲突
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF (因为我们不是基于 Session 的传统 Web 页面，而是前后端分离)
                .csrf().disable()
                // 设置请求授权规则
                .authorizeRequests()
                // .antMatchers("/admin/employee/login").permitAll() // 其实只要放行登录就行
                .anyRequest().permitAll(); // 但为了不影响原来的拦截器逻辑，这里直接全部放行 (Permit All)

        return http.build();
    }
}