package com.siti.sitiapi.service;

import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.AuthRepository;
import com.siti.sitiapi.repository.UserRepository;
import com.siti.sitiapi.repository.PassengerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;
    private final PassengerRepository passengerRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EmailService emailService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;


    public Map<String, Object> getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        Long userId = user.getId();
        String role = "USER";
        String name = user.getName();

        if (authRepository.hasAdministratorById(userId)) {
            role = "ADMIN";
            List<String> names = jdbcTemplate.query("SELECT name FROM administrators WHERE id = ?", (rs, row) -> rs.getString("name"), userId);
            if (!names.isEmpty() && names.get(0) != null) {
                name = names.get(0);
            }
        } else if (authRepository.hasDriverById(userId)) {
            role = "DRIVE";
            List<String> names = jdbcTemplate.query("SELECT name FROM drivers WHERE id = ?", (rs, row) -> rs.getString("name"), userId);
            if (!names.isEmpty() && names.get(0) != null) {
                name = names.get(0);
            }
        }

        if (name == null) {
            name = email.split("@")[0];
        }

        return Map.of(
            "id", userId,
            "email", email,
            "role", role,
            "name", name
        );
    }

    public Map<String, Object> login(String email, String password) {
        Cache cacheSession       = cacheManager.getCache("session");
        Cache cacheUsersActivate = cacheManager.getCache("usersActivate");
        Cache cacheAdminActivate = cacheManager.getCache("usersAdministratorActivate");
        Cache cacheDriverActivate = cacheManager.getCache("usersDriverActivate");
        Cache loginAttempts      = cacheManager.getCache("loginAttempts");

        org.springframework.web.context.request.ServletRequestAttributes attrs = 
            (org.springframework.web.context.request.ServletRequestAttributes) org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
        String ipAddress = attrs != null ? attrs.getRequest().getRemoteAddr() : "unknown";

        if (loginAttempts != null) {
            Integer attempts = loginAttempts.get(ipAddress, Integer.class);
            if (attempts != null && attempts >= 3) {
                throw new RuntimeException("Conta temporariamente bloqueada após 3 tentativas inválidas. Tente novamente mais tarde.");
            }
        }

        User user = authRepository.getUserByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            if (loginAttempts != null) {
                Integer attempts = loginAttempts.get(ipAddress, Integer.class);
                loginAttempts.put(ipAddress, (attempts == null ? 0 : attempts) + 1);
            }
            throw new RuntimeException("Usuário ou senha incorretos.");
        }

        if (loginAttempts != null) {
            loginAttempts.evict(ipAddress);
        }

        if ("Inativo".equalsIgnoreCase(user.getStatus()) || "Pendente".equalsIgnoreCase(user.getStatus())) {
            throw new RuntimeException("Sua conta está inativa. Entre em contato com a administração.");
        }

        Long userId = ((Number) user.getId()).longValue();
        
        String role = "USER";
        if (authRepository.hasAdministratorById(userId)) {
            role = "ADMIN";
        } else if (authRepository.hasDriverById(userId)) {
            role = "DRIVE";
        }

        System.out.println("userId: " + userId + " | role: " + role);

        Cache cacheAtivo = null;
        if ("ADMIN".equals(role)) {
            cacheAtivo = cacheAdminActivate;
        } else if ("DRIVE".equals(role)) {
            cacheAtivo = cacheDriverActivate;
        } else {
            cacheAtivo = cacheUsersActivate;
        }

        if (cacheAtivo != null) {
            Cache.ValueWrapper oldToken = cacheAtivo.get(email);
            if (oldToken != null && oldToken.get() != null && cacheSession != null) {
                cacheSession.evict(oldToken.get().toString());
            }
        }

        String accessKey = UUID.randomUUID().toString();

        if (cacheSession != null) cacheSession.put(accessKey, email);

        if ("ADMIN".equals(role)) {
            if (cacheAdminActivate != null) cacheAdminActivate.put(email, accessKey);
        } else if ("DRIVE".equals(role)) {
            if (cacheDriverActivate != null) cacheDriverActivate.put(email, accessKey);
        } else {
            if (cacheUsersActivate != null) cacheUsersActivate.put(email, accessKey);
        }

        User dbUser = userRepository.findById(userId);
        String name = dbUser != null ? dbUser.getName() : null;
        if ("ADMIN".equals(role)) {
            List<String> names = jdbcTemplate.query("SELECT name FROM administrators WHERE id = ?", (rs, row) -> rs.getString("name"), userId);
            if (!names.isEmpty() && names.get(0) != null) {
                name = names.get(0);
            }
        } else if ("DRIVE".equals(role)) {
            List<String> names = jdbcTemplate.query("SELECT name FROM drivers WHERE id = ?", (rs, row) -> rs.getString("name"), userId);
            if (!names.isEmpty() && names.get(0) != null) {
                name = names.get(0);
            }
        }
        if (name == null) {
            name = email.split("@")[0];
        }

        Map<String, Object> userProfile = Map.of(
            "id", userId,
            "email", email,
            "role", role,
            "name", name
        );

        return Map.of(
            "accessKey", accessKey,
            "token", accessKey,
            "role", role,
            "user", userProfile
        );
    }

    public String getEmailByAccessKey(String accessKey) {
        Cache cache = cacheManager.getCache("session");
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(accessKey);
            if (wrapper != null) {
                return (String) wrapper.get();
            }
        }
        return null;
    }

    public boolean validateRole(String accessKey, String role, String email) {
        if ("ADMIN".equals(role)) {
            Cache cacheAdmin = cacheManager.getCache("usersAdministratorActivate");
            if (cacheAdmin == null) return false;
            Cache.ValueWrapper wrapper = cacheAdmin.get(email);
            return wrapper != null && accessKey.equals(wrapper.get());
        }

        if ("DRIVE".equals(role)) {
            Cache cacheDriver = cacheManager.getCache("usersDriverActivate");
            if (cacheDriver == null) {
                Cache cacheSession = cacheManager.getCache("session");
                if (cacheSession == null) return false;
                Cache.ValueWrapper wrapper = cacheSession.get(accessKey);
                return wrapper != null && email.equals(wrapper.get());
            }
            Cache.ValueWrapper wrapper = cacheDriver.get(email);
            return wrapper != null && accessKey.equals(wrapper.get());
        }

        if ("USER".equals(role)) {
            Cache cacheUser = cacheManager.getCache("usersActivate");
            if (cacheUser == null) return false;
            Cache.ValueWrapper wrapper = cacheUser.get(email);
            return wrapper != null && accessKey.equals(wrapper.get());
        }

        return false;
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("E-mail não encontrado");
        }
        
        String token = UUID.randomUUID().toString();
        Cache cache = cacheManager.getCache("passwordResetTokens");
        if (cache != null) {
            cache.put(token, email);
        }
        
        String link = "http://localhost:5173/reset-password?token=" + token;
        emailService.sendSimpleMessage(email, "SITI - Recuperação de Senha", 
                "Para redefinir sua senha, clique no link a seguir (expira em breve): " + link);
    }

    public void resetPassword(String token, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new RuntimeException("Senha incompatível: A senha deve conter pelo menos 8 caracteres.");
        }
        
        Cache cache = cacheManager.getCache("passwordResetTokens");
        if (cache == null) throw new RuntimeException("Cache não configurado");
        
        Cache.ValueWrapper wrapper = cache.get(token);
        if (wrapper == null) {
            throw new RuntimeException("Token expirado ou inválido.");
        }
        
        String email = (String) wrapper.get();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        
        String hash = passwordEncoder.encode(newPassword);
        jdbcTemplate.update("UPDATE users SET password = ? WHERE id = ?", hash, user.getId());
        
        cache.evict(token);
    }
}