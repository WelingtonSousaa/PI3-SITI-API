package com.siti.sitiapi.repository;

import com.siti.sitiapi.model.Passenger;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PassengerRepository implements BaseRepository {

    private final JdbcTemplate jdbc;

    public void create(
            Long id,
            java.time.LocalDate birthDate,
            String phone,
            String type,
            String registrationNumber,
            String bondProof,
            Long idAddress
    ) {
        SimpleJdbcCall call = new SimpleJdbcCall(jdbc).withProcedureName("ProcCreatePassenger");

        Map<String, Object> params = new HashMap<>();
        params.put("p_id", id);
        params.put("p_birth_date", birthDate);
        params.put("p_phone", phone);
        params.put("p_type", type);
        params.put("p_registration_number", registrationNumber);
        params.put("p_bond_proof", bondProof);
        params.put("p_id_address", idAddress);

        call.execute(params);
    }

    public boolean existsById(Long id) {
        Boolean exists = jdbc.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM passengers WHERE id = ?)",
                Boolean.class,
                id
        );
        return Boolean.TRUE.equals(exists);
    }

    public Passenger findById(Long id) {
        var result = jdbc.query(
                "SELECT id, birth_date, phone, type, registration_number, bond_proof, id_address FROM passengers WHERE id = ?",
                (rs, row) -> {
                    Passenger p = new Passenger();
                    p.setId(rs.getLong("id"));
                    p.setBirthDate(rs.getDate("birth_date") != null
                            ? rs.getDate("birth_date").toLocalDate() : null);
                    p.setPhone(rs.getString("phone"));
                    p.setType(rs.getString("type"));
                    p.setRegistrationNumber(rs.getString("registration_number"));
                    p.setBondProof(rs.getString("bond_proof"));
                    p.setIdAddress(rs.getLong("id_address"));
                    return p;
                },
                id
        );
        return result.isEmpty() ? null : result.get(0);
    }
}
