package com.siti.sitiapi.exception;

public class ProcedureNotFoundException extends RuntimeException {
    public ProcedureNotFoundException(String procedureName) {
        super("Procedure not found or outdated: " + procedureName);
    }
}