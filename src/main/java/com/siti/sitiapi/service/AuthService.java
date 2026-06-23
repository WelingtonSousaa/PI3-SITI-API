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
        Cache cacheDriverActivate = cacheManager.getCache("usersDriverActivate"); // Novo cache para Motoristas

        User user = authRepository.getUserByEmailAndPassword(email, password);

        if (user == null || user.getId() == null) {
            throw new RuntimeException("Usuário ou senha incorretos.");
        }

        Long userId = ((Number) user.getId()).longValue();
        boolean isAdmin = authRepository.hasAdministratorById(userId);
        boolean isDriver = authRepository.hasDriverById(userId); // Verifica se é motorista

        System.out.println("userId: " + userId + " | isAdmin: " + isAdmin + " | isDriver: " + isDriver);

        // Define o cache ativo com base na role para limpar o token antigo
        Cache cacheAtivo = null;
        if (isAdmin) {
            cacheAtivo = cacheAdminActivate;
        } else if (isDriver) {
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

        if (isAdmin) {
            if (cacheAdminActivate != null) {
                cacheAdminActivate.put(email, accessKey);
            }
            return Map.of("accessKey", accessKey, "role", "ADMIN");
        }

        if (isDriver) {
            if (cacheDriverActivate != null) {
                cacheDriverActivate.put(email, accessKey);
            }
            return Map.of("accessKey", accessKey, "role", "DRIVE"); // Retorna DRIVE mesmo se o cache de validação for null
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

        if ("DRIVE".equals(role)) { // Validação para a role DRIVE
            Cache cacheDriver = cacheManager.getCache("usersDriverActivate");
            if (cacheDriver == null) {
                // Fallback: se o cache de motorista for null, valida pelo cache de sessão geral
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
}