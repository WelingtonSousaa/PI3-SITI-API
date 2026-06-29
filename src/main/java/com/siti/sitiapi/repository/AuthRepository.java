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
public class AuthRepository implements BaseRepository{

    private final JdbcTemplate jdbcTemplate;

    public User getUserByEmailAndPassword(String email, String password) {
        List<User> result = jdbcTemplate.query("SELECT id, email FROM users WHERE email = ? AND password = ?", (rs, row) -> {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setEmail(rs.getString("email"));
            return u;
        }, email, password);
        return result.isEmpty() ? null : result.getFirst();
    }

    public boolean hasAdministratorById(Long id) {
        Boolean result = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM administrators WHERE id = ?)",
                Boolean.class,
                id
        );
        return Boolean.TRUE.equals(result);
    }

    public boolean hasDriverById(Long id) {
        Boolean result = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM drivers WHERE id = ?)",
                Boolean.class,
                id
        );
        return Boolean.TRUE.equals(result);
    }

}