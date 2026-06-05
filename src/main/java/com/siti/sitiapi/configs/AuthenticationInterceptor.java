package com.siti.sitiapi.configs;

import com.siti.sitiapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String accessKey = request.getHeader("Authorization");
        String role      = request.getHeader("Role");

        if (accessKey != null && role != null) {
            String tokenPuro   = accessKey.replace("Bearer ", "").trim();
            String userActivate = authService.getEmailByAccessKey(tokenPuro);

            if (userActivate != null) {
                boolean validRole = authService.validateRole(tokenPuro, role, userActivate);

                if (!validRole) {
                    deny(response, "Access denied: invalid role.");
                    return false;
                }

                request.setAttribute("userActivate", userActivate);
                request.setAttribute("role", role);
                return true;
            }
        }

        deny(response, "Access denied.");
        return false;
    }

    private void deny(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}