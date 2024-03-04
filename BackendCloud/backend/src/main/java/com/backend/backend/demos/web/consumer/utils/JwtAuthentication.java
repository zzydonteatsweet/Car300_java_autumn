package com.backend.backend.demos.web.consumer.utils;

import com.backend.backend.demos.web.utils.JwtUtil;
import io.jsonwebtoken.Claims;

public class JwtAuthentication {
    public static Integer getUserId(String token) {
        int userId = -1;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userId = Integer.parseInt(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}
