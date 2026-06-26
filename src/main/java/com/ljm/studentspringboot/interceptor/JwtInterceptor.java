package com.ljm.studentspringboot.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljm.studentspringboot.entity.Result;
import com.ljm.studentspringboot.util.JwtUtil;
import com.ljm.studentspringboot.util.UserContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        UserContext.clear();
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization == null || authorization.isBlank()) {
            writeError(response, "未登录，请先登录");
            return false;
        }

        if (!authorization.startsWith(BEARER_PREFIX)) {
            writeError(response, "token格式错误");
            return false;
        }

        String token = authorization.substring(BEARER_PREFIX.length()).trim();
        if (token.isBlank()) {
            writeError(response, "token格式错误");
            return false;
        }

        try {
            Claims claims = JwtUtil.parseToken(token);
            UserContext.setUser(getUserId(claims), claims.get("username", String.class));
            return true;
        } catch (ExpiredJwtException e) {
            writeError(response, "登录已过期，请重新登录");
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            writeError(response, "token无效，请重新登录");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private Long getUserId(Claims claims) {
        Object userId = claims.get("userId");
        if (userId instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(userId.toString());
    }

    private void writeError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), Result.error(message));
    }
}
