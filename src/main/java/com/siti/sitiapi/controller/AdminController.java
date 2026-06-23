package com.siti.sitiapi.controller;

import com.siti.sitiapi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private boolean isNotAdmin(String role) {
        return !"ADMIN".equals(role);
    }

    @GetMapping("/pending-homologations")
    public ResponseEntity<?> getPendingHomologations(@RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminService.getPendingHomologations());
    }

    @PostMapping("/homologate/{id}")
    public ResponseEntity<?> homologate(@PathVariable Long id, @RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            adminService.homologate(id);
            return ResponseEntity.ok(Map.of("success", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id, @RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            adminService.reject(id);
            return ResponseEntity.ok(Map.of("success", true, "id", id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/routes")
    public ResponseEntity<?> getRoutes(@RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminService.getRoutes());
    }

    @GetMapping("/vehicles")
    public ResponseEntity<?> getVehicles(@RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminService.getVehicles());
    }

    @PostMapping("/vehicles")
    public ResponseEntity<?> createVehicle(@RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Map<String, Object> result = adminService.createVehicle(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/drivers")
    public ResponseEntity<?> getDrivers(@RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminService.getDrivers());
    }

    @PostMapping("/drivers")
    public ResponseEntity<?> createDriver(@RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Map<String, Object> result = adminService.createDriver(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings(@RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminService.getSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Map<String, Object> result = adminService.updateSettings(payload);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/notices")
    public ResponseEntity<?> createNotice(@RequestBody Map<String, Object> payload, @RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        try {
            Map<String, Object> result = adminService.createNotice(payload);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/support-messages")
    public ResponseEntity<?> getSupportMessages(@RequestAttribute("role") String role) {
        if (isNotAdmin(role)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(adminService.getSupportMessages());
    }
}
