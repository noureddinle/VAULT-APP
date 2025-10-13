package com.datavault.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datavault.Models.AccessRequest;
import com.datavault.Services.AccessRequestService;

@RestController
@PreAuthorize("hasRole('USER')")
@RequestMapping("/api/access")
public class AccessRequestController {

    @Autowired
    private AccessRequestService accessService;

    @PostMapping("/user/{userId}")
    public AccessRequest createAccessRequest(@PathVariable UUID userId, @RequestBody AccessRequest request) {
        return accessService.createAccessRequest(userId, request);
    }

    @GetMapping("/user/{userId}")
    public List<AccessRequest> getRequestsByUser(@PathVariable UUID userId) {
        return accessService.getRequestsByUser(userId);
    }

    @PostMapping("/{requestId}/approve")
    public AccessRequest approveRequest(@PathVariable UUID requestId) {
        return accessService.approveRequest(requestId);
    }

    @PostMapping("/{requestId}/revoke")
    public AccessRequest revokeRequest(@PathVariable UUID requestId) {
        return accessService.revokeRequest(requestId);
    }
}
