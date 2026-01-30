package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import io.jsonwebtoken.ExpiredJwtException;
/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String token = request.getHeader(jwtProperties.getUserTokenName());

        try {
            log.info("jwt校验: {}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            BaseContext.setCurrentId(userId);
            log.info("当前用户id: {}", userId);
            return true;
        } catch (ExpiredJwtException ex) {
            // 专门处理 Token 过期的情况
            log.warn("jwt令牌已过期: {}", ex.getMessage());
            response.setStatus(401);
            // 如果你的项目有统一响应格式，可以在这里返回具体的 JSON 错误信息
            response.getWriter().write("Token expired");
            return false;
        } catch (Exception ex) {
            // 处理其他校验失败（如签名错误、Token 格式错误等）
            log.error("jwt校验失败: {}", ex.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
