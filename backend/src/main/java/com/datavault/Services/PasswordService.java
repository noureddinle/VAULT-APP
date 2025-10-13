package com.datavault.Services;

import com.datavault.Dto.PasswordResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datavault.Models.Password;
import com.datavault.Models.User;
import com.datavault.Repositories.PasswordRepository;
import com.datavault.Repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;

    @Transactional
    public PasswordResponse addPassword(UUID userId, Password passwordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        passwordRequest.setUser(user);
        passwordRequest.setCreatedAt(LocalDateTime.now());
        passwordRequest.setUpdatedAt(LocalDateTime.now());

        return PasswordResponse.fromEntity(passwordRepository.save(passwordRequest));
    }

    public List<PasswordResponse> getAllPasswords(UUID userId) {
        return passwordRepository.findByUserId(userId).stream()
                .map(PasswordResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public PasswordResponse updatePassword(UUID userId, UUID passwordId, Password updatedData) {
        Password existing = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        existing.setServiceName(updatedData.getServiceName());
        existing.setUsername(updatedData.getUsername());
        existing.setEncryptedPassword(updatedData.getEncryptedPassword());
        existing.setWebsiteUrl(updatedData.getWebsiteUrl());
        existing.setNotes(updatedData.getNotes());
        existing.setUpdatedAt(LocalDateTime.now());

        return PasswordResponse.fromEntity(passwordRepository.save(existing));
    }

    public void deletePassword(UUID userId, UUID passwordId) {
        Password existing = passwordRepository.findById(passwordId)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        if (!existing.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }

        passwordRepository.delete(existing);
    }
}
