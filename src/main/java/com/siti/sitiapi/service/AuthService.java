package com.siti.sitiapi.service;

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

        Map<String, Object> user = authRepository.getUserByEmailAndPassword(email, password);

        if (user == null || user.get("id") == null) {
            throw new RuntimeException("Usuário ou senha incorretos.");
        }

        Long userId = ((Number) user.get("id")).longValue();

        if (cacheUsersActivate != null) {
            Cache.ValueWrapper oldToken = cacheUsersActivate.get(email);
            if (oldToken != null && cacheSession != null) {
                cacheSession.evict((String) oldToken.get());
            }
        }

        String accessKey = UUID.randomUUID().toString();

        if (cacheSession != null)       cacheSession.put(accessKey, email);
        if (cacheUsersActivate != null) cacheUsersActivate.put(email, accessKey);

        boolean isAdmin = authRepository.hasAdministratorById(userId);
        System.out.println("userId: " + userId + " | isAdmin: " + isAdmin);

        if (isAdmin && cacheAdminActivate != null) {
            cacheAdminActivate.put(email, accessKey);
            return Map.of("accessKey", accessKey, "role", "ADMIN");
        }

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
}