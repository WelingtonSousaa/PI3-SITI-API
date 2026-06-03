package com.siti.sitiapi.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private String path;
    private String timestamp;

    public ErrorResponse(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now().toString();
    }
}