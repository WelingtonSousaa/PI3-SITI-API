package com.siti.sitiapi.dto;

import lombok.Data;

@Data
public class AdminRegisterRequest {
    private String companyName;
    private String cnpj;
    private String city;
    private String state;
    private String email;
    private String password;
}
