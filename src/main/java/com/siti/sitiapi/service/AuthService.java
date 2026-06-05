package com.siti.sitiapi.service;

import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final CacheManager cacheManager;

    public Map<String, String> login(String email, String password) {

        Cache cacheSession       = cacheManager.getCache("session");
        Cache cacheUsersActivate = cacheManager.getCache("usersActivate");
        Cache cacheAdminActivate = cacheManager.getCache("usersAdministratorActivate");

        User user = authRepository.getUserByEmailAndPassword(email, password);

        if (user == null || user.getId() == null) {
            throw new RuntimeException("Usuário ou senha incorretos.");
        }

        Long userId = ((Number) user.getId()).longValue();
        boolean isAdmin = authRepository.hasAdministratorById(userId);

        System.out.println("userId: " + userId + " | isAdmin: " + isAdmin);

        // evict token antigo
        Cache cacheAtivo = isAdmin ? cacheAdminActivate : cacheUsersActivate;
        if (cacheAtivo != null) {
            Cache.ValueWrapper oldToken = cacheAtivo.get(email);
            if (oldToken != null && oldToken.get() != null && cacheSession != null) {
                cacheSession.evict(oldToken.get().toString());
            }
        }

        String accessKey = UUID.randomUUID().toString();

        if (cacheSession != null) cacheSession.put(accessKey, email);

        if (isAdmin && cacheAdminActivate != null) {
            cacheAdminActivate.put(email, accessKey);
            return Map.of("accessKey", accessKey, "role", "ADMIN");
        }

        if (cacheUsersActivate != null) cacheUsersActivate.put(email, accessKey);
        return Map.of("accessKey", accessKey, "role", "USER");
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

        if ("USER".equals(role)) {
            Cache cacheUser = cacheManager.getCache("usersActivate");
            if (cacheUser == null) return false;
            Cache.ValueWrapper wrapper = cacheUser.get(email);
            return wrapper != null && accessKey.equals(wrapper.get());
        }

        return false;
    }
}