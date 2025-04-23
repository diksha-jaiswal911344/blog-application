package com.ql.BlogApplication.config;

import com.ql.BlogApplication.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtUtil.extractEmail(token); // email comes here
            if (email == null || email.isEmpty()) {
                throw new RuntimeException("Invalid token");
            }

//            // Optional: Log or check role/username if needed
//            String username = jwtUtil.extractUsername(token);
//            String role = jwtUtil.extractRole(token);
//
//            // Can set user details in request attribute if needed
//            request.setAttribute("email", email);
//            request.setAttribute("username", username);
//            request.setAttribute("role", role);

            return true; // all okay

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            return false;
        }
    }
}
