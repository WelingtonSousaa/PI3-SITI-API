package com.siti.sitiapi.service;

import com.siti.sitiapi.model.User;
import com.siti.sitiapi.repository.AuthRepository;
import com.siti.sitiapi.repository.PassengerRepository;
import com.siti.sitiapi.repository.UserRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Locale;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private PassengerRepository passengerRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Cache cacheSession;
    @Mock
    private Cache cacheUsersActivate;
    @Mock
    private Cache cacheAdminActivate;
    @Mock
    private Cache cacheDriverActivate;
    @Mock
    private Cache loginAttempts;

    @InjectMocks
    private AuthService authService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("pt", "BR"));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
        lenient().when(cacheManager.getCache(anyString())).thenReturn(null);
    }

    // ==========================================
    // getUserProfileByEmail Tests
    // ==========================================

    @Test
    void testGetUserProfileByEmailUserNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.getUserProfileByEmail("test@test.com"));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testGetUserProfileByEmailAdmin() {
        User user = new User();
        user.setId(1L);
        user.setName("Admin Name");
        
        when(userRepository.findByEmail("admin@test.com")).thenReturn(user);
        when(authRepository.hasAdministratorById(1L)).thenReturn(true);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of("Admin Role Name"));

        Map<String, Object> profile = authService.getUserProfileByEmail("admin@test.com");
        assertEquals("ADMIN", profile.get("role"));
        assertEquals("Admin Role Name", profile.get("name"));
    }

    @Test
    void testGetUserProfileByEmailDriver() {
        User user = new User();
        user.setId(1L);
        user.setName("Driver Name");
        
        when(userRepository.findByEmail("driver@test.com")).thenReturn(user);
        when(authRepository.hasAdministratorById(1L)).thenReturn(false);
        when(authRepository.hasDriverById(1L)).thenReturn(true);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of("Driver Role Name"));

        Map<String, Object> profile = authService.getUserProfileByEmail("driver@test.com");
        assertEquals("DRIVE", profile.get("role"));
        assertEquals("Driver Role Name", profile.get("name"));
    }

    @Test
    void testGetUserProfileByEmailUser() {
        User user = new User();
        user.setId(1L);
        
        when(userRepository.findByEmail("user@test.com")).thenReturn(user);
        when(authRepository.hasAdministratorById(1L)).thenReturn(false);
        when(authRepository.hasDriverById(1L)).thenReturn(false);

        Map<String, Object> profile = authService.getUserProfileByEmail("user@test.com");
        assertEquals("USER", profile.get("role"));
        assertEquals("user", profile.get("name"));
    }

    // ==========================================
    // login Tests
    // ==========================================

    @Test
    void testLoginBlockedAfter3Attempts() {
        when(cacheManager.getCache("loginAttempts")).thenReturn(loginAttempts);
        when(loginAttempts.get("127.0.0.1", Integer.class)).thenReturn(3);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login("test@test.com", "pass"));
        assertEquals("Conta temporariamente bloqueada após 3 tentativas inválidas. Tente novamente mais tarde.", exception.getMessage());
    }

    @Test
    void testLoginWrongPassword() {
        String email = faker.internet().emailAddress();
        User user = new User();
        user.setPassword("encodedPassword");

        when(cacheManager.getCache("loginAttempts")).thenReturn(loginAttempts);
        when(loginAttempts.get("127.0.0.1", Integer.class)).thenReturn(1);
        when(authRepository.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(email, "wrong"));
        assertEquals("Usuário ou senha incorretos.", exception.getMessage());
        verify(loginAttempts, times(1)).put("127.0.0.1", 2);
    }

    @Test
    void testLoginAccountInactive() {
        String email = faker.internet().emailAddress();
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");
        user.setStatus("Inativo");

        when(cacheManager.getCache(anyString())).thenReturn(null);
        when(authRepository.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("pass", "encodedPassword")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(email, "pass"));
        assertEquals("Sua conta está inativa. Entre em contato com a administração.", exception.getMessage());
    }

    @Test
    void testLoginSuccessAdmin() {
        String email = faker.internet().emailAddress();
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");
        user.setStatus("Ativo");
        user.setName("Admin Name");

        when(cacheManager.getCache("session")).thenReturn(cacheSession);
        when(cacheManager.getCache("usersAdministratorActivate")).thenReturn(cacheAdminActivate);
        
        when(authRepository.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("pass", "encodedPassword")).thenReturn(true);
        when(authRepository.hasAdministratorById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(user);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of("Admin Name"));

        Cache.ValueWrapper oldTokenWrapper = mock(Cache.ValueWrapper.class);
        when(oldTokenWrapper.get()).thenReturn("old-token-uuid");
        when(cacheAdminActivate.get(email)).thenReturn(oldTokenWrapper);

        Map<String, Object> result = authService.login(email, "pass");

        assertNotNull(result);
        assertEquals("ADMIN", result.get("role"));
        assertNotNull(result.get("token"));
        verify(cacheSession, times(1)).evict("old-token-uuid");
        verify(cacheAdminActivate, times(1)).put(eq(email), anyString());
    }

    @Test
    void testLoginSuccessDriver() {
        String email = faker.internet().emailAddress();
        User user = new User();
        user.setId(1L);
        user.setPassword("encodedPassword");
        user.setStatus("Ativo");

        when(cacheManager.getCache("session")).thenReturn(cacheSession);
        when(cacheManager.getCache("usersDriverActivate")).thenReturn(cacheDriverActivate);
        
        when(authRepository.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.matches("pass", "encodedPassword")).thenReturn(true);
        when(authRepository.hasAdministratorById(1L)).thenReturn(false);
        when(authRepository.hasDriverById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(user);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of("Driver Name"));

        Map<String, Object> result = authService.login(email, "pass");

        assertNotNull(result);
        assertEquals("DRIVE", result.get("role"));
        verify(cacheDriverActivate, times(1)).put(eq(email), anyString());
    }

    // ==========================================
    // getEmailByAccessKey Tests
    // ==========================================

    @Test
    void testGetEmailByAccessKeySuccess() {
        when(cacheManager.getCache("session")).thenReturn(cacheSession);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("test@test.com");
        when(cacheSession.get("token")).thenReturn(wrapper);

        assertEquals("test@test.com", authService.getEmailByAccessKey("token"));
    }

    @Test
    void testGetEmailByAccessKeyNotFound() {
        when(cacheManager.getCache("session")).thenReturn(cacheSession);
        when(cacheSession.get("token")).thenReturn(null);

        assertNull(authService.getEmailByAccessKey("token"));
    }

    // ==========================================
    // validateRole Tests
    // ==========================================

    @Test
    void testValidateRoleAdminSuccess() {
        when(cacheManager.getCache("usersAdministratorActivate")).thenReturn(cacheAdminActivate);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("token");
        when(cacheAdminActivate.get("admin@test.com")).thenReturn(wrapper);

        assertTrue(authService.validateRole("token", "ADMIN", "admin@test.com"));
    }

    @Test
    void testValidateRoleDriverCacheSuccess() {
        when(cacheManager.getCache("usersDriverActivate")).thenReturn(cacheDriverActivate);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("token");
        when(cacheDriverActivate.get("driver@test.com")).thenReturn(wrapper);

        assertTrue(authService.validateRole("token", "DRIVE", "driver@test.com"));
    }

    @Test
    void testValidateRoleDriverSessionFallback() {
        when(cacheManager.getCache("usersDriverActivate")).thenReturn(null);
        when(cacheManager.getCache("session")).thenReturn(cacheSession);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("driver@test.com");
        when(cacheSession.get("token")).thenReturn(wrapper);

        assertTrue(authService.validateRole("token", "DRIVE", "driver@test.com"));
    }

    @Test
    void testValidateRoleUserSuccess() {
        when(cacheManager.getCache("usersActivate")).thenReturn(cacheUsersActivate);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("token");
        when(cacheUsersActivate.get("user@test.com")).thenReturn(wrapper);

        assertTrue(authService.validateRole("token", "USER", "user@test.com"));
    }
    
    @Test
    void testValidateRoleInvalidRole() {
        assertFalse(authService.validateRole("token", "INVALID", "user@test.com"));
    }

    // ==========================================
    // forgotPassword Tests
    // ==========================================

    @Test
    void testForgotPasswordUserNotFound() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.forgotPassword("test@test.com"));
        assertEquals("E-mail não encontrado", exception.getMessage());
    }

    @Test
    void testForgotPasswordSuccess() {
        User user = new User();
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(cacheManager.getCache("passwordResetTokens")).thenReturn(cacheSession); // Reusing cacheSession mock for simplicity
        
        authService.forgotPassword("test@test.com");
        
        verify(cacheSession, times(1)).put(anyString(), eq("test@test.com"));
        verify(emailService, times(1)).sendSimpleMessage(eq("test@test.com"), anyString(), anyString());
    }

    // ==========================================
    // resetPassword Tests
    // ==========================================

    @Test
    void testResetPasswordTooShort() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.resetPassword("token", "123"));
        assertEquals("Senha incompatível: A senha deve conter pelo menos 8 caracteres.", exception.getMessage());
    }

    @Test
    void testResetPasswordInvalidToken() {
        when(cacheManager.getCache("passwordResetTokens")).thenReturn(cacheSession);
        when(cacheSession.get("token")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.resetPassword("token", "longpassword"));
        assertEquals("Token expirado ou inválido.", exception.getMessage());
    }

    @Test
    void testResetPasswordUserNotFound() {
        when(cacheManager.getCache("passwordResetTokens")).thenReturn(cacheSession);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("test@test.com");
        when(cacheSession.get("token")).thenReturn(wrapper);
        when(userRepository.findByEmail("test@test.com")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.resetPassword("token", "longpassword"));
        assertEquals("Usuário não encontrado.", exception.getMessage());
    }

    @Test
    void testResetPasswordSuccess() {
        when(cacheManager.getCache("passwordResetTokens")).thenReturn(cacheSession);
        Cache.ValueWrapper wrapper = mock(Cache.ValueWrapper.class);
        when(wrapper.get()).thenReturn("test@test.com");
        when(cacheSession.get("token")).thenReturn(wrapper);
        
        User user = new User();
        user.setId(1L);
        when(userRepository.findByEmail("test@test.com")).thenReturn(user);
        when(passwordEncoder.encode("longpassword")).thenReturn("newHash");

        authService.resetPassword("token", "longpassword");

        verify(jdbcTemplate, times(1)).update("UPDATE users SET password = ? WHERE id = ?", "newHash", 1L);
        verify(cacheSession, times(1)).evict("token");
    }
}
