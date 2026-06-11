package com.siti.sitiapi.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Procedures {

    public static void procCreateUser(
            Connection conn,
            String p_email,
            String p_password,
            String p_identifier_document
    ) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (email, password, status, identifier_document) VALUES (?, ?, 'active', ?)")) {
            stmt.setString(1, p_email);
            stmt.setString(2, p_password);
            stmt.setString(3, p_identifier_document);
            stmt.executeUpdate();
        }
    }

    public static ResultSet procGetUserByEmail(Connection conn, String p_email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, email, status, identifier_document FROM users WHERE email = ?"
        );
        stmt.setString(1, p_email);
        return stmt.executeQuery();
    }

    public static ResultSet procExistUserByEmail(Connection conn, String p_email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM users WHERE email = ?) AS exists_user"
        );
        stmt.setString(1, p_email);
        return stmt.executeQuery();
    }

    public static ResultSet procGetUserByEmailAndPassword(Connection conn, String p_email, String p_password) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, email FROM users WHERE email = ? AND password = ?"
        );
        stmt.setString(1, p_email);
        stmt.setString(2, p_password);
        return stmt.executeQuery();
    }

    public static ResultSet hasUserAdministratorById(Connection conn, Long p_id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM administrators WHERE id = ?) AS result"
        );
        if (p_id == null) {
            stmt.setNull(1, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(1, p_id);
        }
        return stmt.executeQuery();
    }
}
