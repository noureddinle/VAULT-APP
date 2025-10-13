package com.datavault.Controllers;

import com.datavault.Dto.PasswordResponse;

import com.datavault.Models.Password;
import com.datavault.Security.JwtTokenProvider;
import com.datavault.Services.PasswordService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/passwords")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;

    private UUID extractUserId(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    @PostMapping
    public ResponseEntity<PasswordResponse> addPassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Password passwordRequest) {
        UUID userId = extractUserId(authHeader);
        PasswordResponse saved = passwordService.addPassword(userId, passwordRequest);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<PasswordResponse>> getPasswords(
            @RequestHeader("Authorization") String authHeader) {
        UUID userId = extractUserId(authHeader);
        return ResponseEntity.ok(passwordService.getAllPasswords(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasswordResponse> updatePassword(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id,
            @RequestBody Password updatedData) {
        UUID userId = extractUserId(authHeader);
        return ResponseEntity.ok(passwordService.updatePassword(userId, id, updatedData));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable UUID id) {
        UUID userId = extractUserId(authHeader);
        passwordService.deletePassword(userId, id);
        return ResponseEntity.noContent().build();
    }
}
