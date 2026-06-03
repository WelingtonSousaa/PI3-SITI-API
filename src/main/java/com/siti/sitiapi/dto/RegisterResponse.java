package com.siti.sitiapi.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private Long id;
    private String email;
    private String apiKey;
}