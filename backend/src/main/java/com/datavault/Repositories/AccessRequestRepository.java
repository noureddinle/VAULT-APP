package com.datavault.Repositories;

import com.datavault.Models.AccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface AccessRequestRepository extends JpaRepository<AccessRequest, UUID> {
    List<AccessRequest> findByUserId(UUID userId);
}
