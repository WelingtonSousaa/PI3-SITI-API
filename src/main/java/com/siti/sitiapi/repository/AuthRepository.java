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

    public Map<String, Object> getUserByEmailAndPassword(String email, String password) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("ProcGetUserByEmailAndPassword")
                .returningResultSet("user", (rs, rowNum) -> Map.of(
                        "id",    rs.getLong("id"),
                        "email", rs.getString("email")
                ));

        Map<String, Object> result = call.execute(Map.of(
                "p_email",    email,
                "p_password", password
        ));

        List<Map<String, Object>> users = (List<Map<String, Object>>) result.get("user");

        if (users == null || users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public boolean hasAdministratorById(Long id) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("HasUserAdministratorById")
                .returningResultSet("result", (rs, rowNum) -> rs.getInt("result"));

        Map<String, Object> output = call.execute(Map.of("p_id", id));

        List<Integer> resultList = (List<Integer>) output.get("result");
        return resultList != null && !resultList.isEmpty() && resultList.get(0) == 1;
    }
}