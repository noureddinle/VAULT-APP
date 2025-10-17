package com.datavault.Services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datavault.Dto.AuthResponse;
import com.datavault.Dto.LoginRequest;
import com.datavault.Dto.RegisterRequest;
import com.datavault.Models.User;
import com.datavault.Repositories.UserRepository;
import com.datavault.Security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPublicKey(request.getPublicKey());
        user.setEncryptedMasterKey(request.getEncryptedMasterKey());
        user.setRole("USER");
        
        user = userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .role(user.getRole())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());
        
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .role(user.getRole())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .encryptedMasterKey(user.getEncryptedMasterKey())
                .build();
    }
}