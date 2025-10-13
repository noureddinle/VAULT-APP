package com.datavault.Controllers;

import com.datavault.Models.AccessRequest;
import com.datavault.Repositories.AccessRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/access")
public class AccessController {

    @Autowired
    private AccessRequestRepository accessRepo;

    @PostMapping("/request")
    public AccessRequest requestAccess(@RequestBody AccessRequest request) {
        return accessRepo.save(request);
    }

    @GetMapping("/user/{userId}")
    public List<AccessRequest> getUserRequests(@PathVariable UUID userId) {
        return accessRepo.findByUserId(userId);
    }

    @PostMapping("/{id}/approve")
    public AccessRequest approveAccess(@PathVariable UUID id) {
        AccessRequest request = accessRepo.findById(id).orElseThrow();
        request.setApproved(true);
        return accessRepo.save(request);
    }

    @PostMapping("/{id}/revoke")
    public AccessRequest revokeAccess(@PathVariable UUID id) {
        AccessRequest request = accessRepo.findById(id).orElseThrow();
        request.setApproved(false);
        return accessRepo.save(request);
    }
}
