package com.datavault.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.datavault.Models.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Optional<Document> findBySha256(String sha256);
}
