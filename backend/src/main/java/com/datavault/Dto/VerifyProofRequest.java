package com.datavault.Dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyProofRequest {
    @NotBlank(message = "Document hash is required")
    private String docHash;
    @NotBlank(message = "Document type is required")
    private String docType;
}
