package com.siti.sitiapi.repository;

import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import com.siti.sitiapi.model.Driver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class DriverRepository {
    private final JdbcTemplate jdbc;

    public void create(
            Long id,
            String cnhNumber,
            String cnhCategory,
            String name,
            java.time.LocalDate birthDate,
            java.time.LocalDate cnhValidityDate,
            String phone,
            Long idAddress) {
        SimpleJdbcCall call = new SimpleJdbcCall(java.util.Objects.requireNonNull(jdbc)).withProcedureName("ProcCreateDriver");

        Map<String, Object> params = new HashMap<>();
        params.put("p_id", id);
        params.put("p_cnh_number", cnhNumber);
        params.put("p_cnh_category", cnhCategory);
        params.put("p_name", name);
        params.put("p_birth_date", birthDate);
        params.put("p_cnh_validity_date", cnhValidityDate);
        params.put("p_phone", phone);
        params.put("p_id_address", idAddress);

        call.execute(params);
    }

    public void validateDriver(Driver driver) throws Exception {
        var validation = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM drivers WHERE cnh_number = ? AND id != ?) ",
                Boolean.class,
                driver.getCnhNumber(),
                driver.getId());

        if (Boolean.TRUE.equals(validation)) {
            throw new Exception("Driver with CNH number " + driver.getCnhNumber() + " already exists.");
        }
    }

    public boolean existsById(Long id) {
        Boolean exists = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM drivers WHERE id = ?)",
                Boolean.class,
                id);
        return Boolean.TRUE.equals(exists);
    }

    public Driver findById(Long id) {
        var result = jdbc.query(
                "SELECT id, cnh_number, cnh_category, name, birth_date, cnh_validity_date, phone, id_address FROM drivers WHERE id = ?",
                (rs, row) -> {
                    Driver d = new Driver();
                    d.setId(rs.getLong("id"));
                    d.setCnhNumber(rs.getString("cnh_number"));
                    String cnhCategoryStr = rs.getString("cnh_category");
                    d.setCnhCategory(cnhCategoryStr != null ? com.siti.sitiapi.enums.CNHCategoryEnum.valueOf(cnhCategoryStr) : null);
                    d.setName(rs.getString("name"));
                    d.setBirthDate(rs.getDate("birth_date") != null
                            ? rs.getDate("birth_date").toLocalDate()
                            : null);
                    d.setCnhValidityDate(rs.getDate("cnh_validity_date") != null
                            ? rs.getDate("cnh_validity_date").toLocalDate()
                            : null);
                    d.setPhone(rs.getString("phone"));
                    d.setIdAddress(rs.getLong("id_address"));
                    return d;
                },
                id);
        return result.isEmpty() ? null : result.get(0);
    }
}
