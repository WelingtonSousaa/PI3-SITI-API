package com.siti.sitiapi.service;

import com.siti.sitiapi.dto.LoginRequest;
import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.AuthRepository;
import com.siti.sitiapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheManager cacheManager;
    
    @Mock
    private Cache cache;
    
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@siti.edu.br");
        user.setName("Admin Teste");
        user.setStatus("Ativo");

        when(authRepository.getUserByEmailAndPassword("admin@siti.edu.br", "123456")).thenReturn(user);
        when(authRepository.hasAdministratorById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(user);
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        Map<String, Object> response = authService.login("admin@siti.edu.br", "123456");

        assertNotNull(response);
        assertNotNull(response.get("token"));
        assertEquals("ADMIN", response.get("role"));
    }

    @Test
    void testLoginInvalidPassword() {
        when(authRepository.getUserByEmailAndPassword("admin@siti.edu.br", "wrongpassword")).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> authService.login("admin@siti.edu.br", "wrongpassword"));
        assertEquals("Usuário ou senha incorretos.", exception.getMessage());
    }
}
