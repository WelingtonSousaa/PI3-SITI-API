package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepository implements BaseRepository{

    private final JdbcTemplate jdbc;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public void create(
            String email,
            String password,
            String identifierDocument,
            String name
    ) {
        String hash = passwordEncoder.encode(password);
        jdbc.update("INSERT INTO users (email, password, status, identifier_document, name) VALUES (?, ?, 'Pendente', ?, ?)",
                email, hash, identifierDocument, name);
    }

    public void createAdmin(
            String email,
            String password,
            String cnpj,
            String companyName,
            String city,
            String state
    ) {
        String hash = passwordEncoder.encode(password);
        jdbc.update("INSERT INTO users (email, password, status, identifier_document, name) VALUES (?, ?, 'Ativo', ?, ?)",
                email, hash, cnpj, companyName);
        
        Long userId = jdbc.queryForObject("SELECT id FROM users WHERE email = ?", Long.class, email);
        
        jdbc.update("INSERT INTO administrators (id, name, city, state) VALUES (?, ?, ?, ?)",
                userId, companyName, city, state);
    }

    public boolean existsByEmail(String email) {
        Boolean exists = jdbc.queryForObject("SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)", Boolean.class, email);
        return Boolean.TRUE.equals(exists);
    }

    public User findById(Long id) {
        List<User> result = jdbc.query(
                "SELECT id, email, status, identifier_document, name FROM users WHERE id = ?",
                (rs, row) -> {
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    u.setEmail(rs.getString("email"));
                    u.setStatus(rs.getString("status"));
                    u.setIdentifierDocument(rs.getString("identifier_document"));
                    u.setName(rs.getString("name"));
                    return u;
                },
                id
        );
        return result.isEmpty() ? null : result.getFirst();
    }

    public User findByEmail(String email) {
        List<User> result = jdbc.query("SELECT id, email, status, identifier_document, name FROM users WHERE email = ?", (rs, row) -> {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setEmail(rs.getString("email"));
            u.setStatus(rs.getString("status"));
            u.setIdentifierDocument(rs.getString("identifier_document"));
            u.setName(rs.getString("name"));
            return u;
        }, email);
        return result.isEmpty() ? null : result.getFirst();
    }
}