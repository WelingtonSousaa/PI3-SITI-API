package com.siti.sitiapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final CacheManager cacheManager;

    public String login(String email, String password) {

        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
        Integer result = jdbcTemplate.queryForObject(sql, Integer.class, email, password);

        if (result != null && result > 0) {

            Cache cacheSession = cacheManager.getCache("session");
            Cache cacheUsersActivate = cacheManager.getCache("usersActivate");

            if (cacheSession != null && cacheUsersActivate != null) {

                Cache.ValueWrapper oldTokenWrapper = cacheUsersActivate.get(email);
                if (oldTokenWrapper != null) {
                    String oldToken = (String) oldTokenWrapper.get();

                    if (oldToken != null) {
                        cacheSession.evict(oldToken);
                    }
                }

                String accessKey = UUID.randomUUID().toString();
                cacheSession.put(accessKey, email);
                cacheUsersActivate.put(email, accessKey);

                return accessKey;
            }
        }

        throw new RuntimeException("Usuário ou senha incorretos.");
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