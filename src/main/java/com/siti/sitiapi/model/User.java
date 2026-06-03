package com.siti.sitiapi.model;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String email;
    private String password;
    private String status;
    private String identifierDocument;
    private String apiKey;
}