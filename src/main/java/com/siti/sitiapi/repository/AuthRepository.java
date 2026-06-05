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
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public User getUserByEmailAndPassword(String email, String password) {
        List<User> result = jdbcTemplate.query("CALL ProcGetUserByEmailAndPassword(?, ?)", (rs, row) -> {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setEmail(rs.getString("email"));
            return u;
        }, email, password);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean hasAdministratorById(Long id) {
        List<Integer> result = jdbcTemplate.query(
                "CALL HasUserAdministratorById(?)",
                (rs, row) -> rs.getInt("result"),
                id
        );
        return !result.isEmpty() && result.get(0) == 1;
    }
}