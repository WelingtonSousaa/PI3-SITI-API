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
            String p_identifier_document,
            String p_name
    ) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (email, password, status, identifier_document, name) VALUES (?, ?, 'active', ?, ?)")) {
            stmt.setString(1, p_email);
            stmt.setString(2, p_password);
            stmt.setString(3, p_identifier_document);
            stmt.setString(4, p_name);
            stmt.executeUpdate();
        }
    }

    public static ResultSet procGetUserByEmail(Connection conn, String p_email) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, email, status, identifier_document, name FROM users WHERE email = ?"
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

    public static ResultSet hasUserDriverById(Connection conn, Long p_id) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT EXISTS(SELECT 1 FROM drivers WHERE id = ?) AS result"
        );
        if (p_id == null) {
            stmt.setNull(1, java.sql.Types.BIGINT);
        } else {
            stmt.setLong(1, p_id);
        }
        return stmt.executeQuery();
    }

    public static void procCreatePassenger(
            Connection conn,
            Long p_id,
            java.sql.Date p_birth_date,
            String p_phone,
            String p_type,
            String p_registration_number,
            String p_bond_proof,
            Long p_id_address
    ) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO passengers (id, birth_date, phone, type, registration_number, bond_proof, id_address) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setLong(1, p_id);
            stmt.setDate(2, p_birth_date);
            stmt.setString(3, p_phone);
            stmt.setString(4, p_type);
            stmt.setString(5, p_registration_number);
            stmt.setString(6, p_bond_proof);
            if (p_id_address == null) {
                stmt.setNull(7, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(7, p_id_address);
            }
            stmt.executeUpdate();
        }
    }

    public static void procCreateDriver(
            Connection conn,
            Long p_id,
            String p_cnh_number,
            String p_cnh_category,
            String p_name,
            java.sql.Date p_birth_date,
            java.sql.Date p_cnh_validity_date,
            String p_phone,
            Long p_id_address
    ) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO drivers (id, cnh_number, cnh_category, name, birth_date, cnh_validity_date, phone, id_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setLong(1, p_id);
            stmt.setString(2, p_cnh_number);
            stmt.setString(3, p_cnh_category);
            stmt.setString(4, p_name);
            stmt.setDate(5, p_birth_date);
            stmt.setDate(6, p_cnh_validity_date);
            stmt.setString(7, p_phone);
            if (p_id_address == null) {
                stmt.setNull(8, java.sql.Types.BIGINT);
            } else {
                stmt.setLong(8, p_id_address);
            }
            stmt.executeUpdate();
        }
    }
}
