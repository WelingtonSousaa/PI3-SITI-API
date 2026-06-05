package com.siti.sitiapi.repository;

import com.siti.sitiapi.exception.ProcedureNotFoundException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
public interface BaseRepository {

    default <T> List<T> callProcedure(JdbcTemplate jdbc, String procedureName, RowMapper<T> mapper, Object... args) {
        try {
            String sql = buildCall(procedureName, args.length);
            return jdbc.query(sql, mapper, args);
        } catch (BadSqlGrammarException e) {
            throw new ProcedureNotFoundException(procedureName);
        }
    }

    default void callProcedureUpdate(JdbcTemplate jdbc, String procedureName, Object... args) {
        try {
            String sql = buildCall(procedureName, args.length);
            jdbc.update(sql, args);
        } catch (BadSqlGrammarException e) {
            throw new ProcedureNotFoundException(procedureName);
        }
    }

    private String buildCall(String procedureName, int argCount) {
        String placeholders = "?,".repeat(argCount);
        if (!placeholders.isEmpty()) {
            placeholders = placeholders.substring(0, placeholders.length() - 1);
        }
        return "CALL " + procedureName + "(" + placeholders + ")";
    }
}