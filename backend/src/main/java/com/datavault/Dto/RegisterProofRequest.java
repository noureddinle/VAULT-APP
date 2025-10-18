package com.datavault.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class RegisterProofRequest {
    @NotBlank(message = "Document hash is required")
    private String docHash;
    
    @NotBlank(message = "Document type is required")
    private String docType;
}

@Data
class VerifyProofRequest {
    @NotBlank(message = "Document hash is required")
    private String docHash;
}

@Data
@lombok.Builder
class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}