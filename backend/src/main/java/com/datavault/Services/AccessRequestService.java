package com.datavault.Services;

import com.datavault.Models.AccessRequest;
import com.datavault.Models.User;
import com.datavault.Repositories.AccessRequestRepository;
import com.datavault.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccessRequestService {

    @Autowired
    private AccessRequestRepository accessRepo;

    @Autowired
    private UserRepository userRepo;

    public AccessRequest createAccessRequest(UUID userId, AccessRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        request.setUser(user);
        request.setCreatedAt(LocalDateTime.now());
        return accessRepo.save(request);
    }

    public List<AccessRequest> getRequestsByUser(UUID userId) {
        return accessRepo.findByUserId(userId);
    }

    public AccessRequest approveRequest(UUID requestId) {
        AccessRequest request = accessRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setApproved(true);
        request.setUpdatedAt(LocalDateTime.now());
        return accessRepo.save(request);
    }

    public AccessRequest revokeRequest(UUID requestId) {
        AccessRequest request = accessRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setApproved(false);
        request.setUpdatedAt(LocalDateTime.now());
        return accessRepo.save(request);
    }

    public void deleteExpiredRequests() {
        List<AccessRequest> all = accessRepo.findAll();
        all.stream()
           .filter(r -> r.getExpiresAt() != null && r.getExpiresAt().isBefore(LocalDateTime.now()))
           .forEach(r -> accessRepo.delete(r));
    }
}
