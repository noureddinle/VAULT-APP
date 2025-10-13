package com.datavault.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.datavault.Models.Password;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PasswordResponse {
    private UUID id;
    private String serviceName;
    private String username;
    private String encryptedPassword;
    private String websiteUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PasswordResponse fromEntity(Password password) {
        return PasswordResponse.builder()
                .id(password.getId())
                .serviceName(password.getServiceName())
                .username(password.getUsername())
                .encryptedPassword(password.getEncryptedPassword())
                .websiteUrl(password.getWebsiteUrl())
                .notes(password.getNotes())
                .createdAt(password.getCreatedAt())
                .updatedAt(password.getUpdatedAt())
                .build();
    }
}
