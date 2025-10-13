package com.datavault.Models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String passwordHash;
    
    private String fullName;
    
    @Column(columnDefinition = "TEXT")
    private String publicKey;
    
    @Column(columnDefinition = "TEXT")
    private String encryptedMasterKey;
    
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private Boolean isVerified = false;
}