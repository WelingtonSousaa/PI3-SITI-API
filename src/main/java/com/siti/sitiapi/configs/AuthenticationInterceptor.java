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

        if (accessKey != null) {
            String tokenPuro = accessKey.replace("Bearer ", "").trim();
            String userActivate = authService.getEmailByAccessKey(tokenPuro);

            if (userActivate != null) {
                request.setAttribute("userActivate", userActivate);
                return true;
            }
        }

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write("{\"erro\": \"Acesso negado.\"}");

        return false;
    }
}