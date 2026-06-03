package com.siti.sitiapi.exception;

import com.siti.sitiapi.dto.ErrorResponse;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private final ErrorResponse error;

    public BusinessException(ErrorResponse error) {
        super(error.getMessage());
        this.error = error;
    }
}