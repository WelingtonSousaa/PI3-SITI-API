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

    public void create(
            String email,
            String password,
            String identifierDocument
    ) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc).withProcedureName("ProcCreateUser");
        call.execute(Map.of(
                "p_email", email,
                "p_password", password,
                "p_identifier_document", identifierDocument
        ));
    }

    public boolean existsByEmail(String email) {
        Boolean exists = jdbc.queryForObject("CALL ProcExistUserByEmail(?)", Boolean.class, email);
        return Boolean.TRUE.equals(exists);
    }

    public User findById(Long id) {
        List<User> result = jdbc.query(
                "SELECT id, email, status, identifier_document FROM users WHERE id = ?",
                (rs, row) -> {
                    User u = new User();
                    u.setId(rs.getLong("id"));
                    u.setEmail(rs.getString("email"));
                    u.setStatus(rs.getString("status"));
                    u.setIdentifierDocument(rs.getString("identifier_document"));
                    return u;
                },
                id
        );
        return result.isEmpty() ? null : result.get(0);
    }

    public User findByEmail(String email) {
        List<User> result = jdbc.query("CALL ProcGetUserByEmail(?)", (rs, row) -> {
            User u = new User();
            u.setId(rs.getLong("id"));
            u.setEmail(rs.getString("email"));
            u.setStatus(rs.getString("status"));
            u.setIdentifierDocument(rs.getString("identifier_document"));
            return u;
        }, email);
        return result.isEmpty() ? null : result.get(0);
    }
}