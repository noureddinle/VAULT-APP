package com.datavault.Repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datavault.Models.Password;

@Repository
public interface PasswordRepository extends JpaRepository<Password, UUID> {
    List<Password> findByUserId(UUID userId);
    List<Password> findByUserIdAndServiceNameContainingIgnoreCase(UUID userId, String serviceName);
}