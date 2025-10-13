package com.datavault.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    private String fullName;
    
    @NotBlank(message = "Public key is required")
    private String publicKey;
    
    @NotBlank(message = "Encrypted master key is required")
    private String encryptedMasterKey;
}