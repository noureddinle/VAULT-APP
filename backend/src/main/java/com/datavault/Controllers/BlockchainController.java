package com.datavault.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datavault.Dto.ApiResponse;
import com.datavault.Dto.RegisterProofRequest;
import com.datavault.Dto.VerifyProofRequest;
import com.datavault.Models.ProofDetails;
import com.datavault.Services.BlockchainService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/blockchain")
@RequiredArgsConstructor
public class BlockchainController {

    private final BlockchainService blockchainService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> registerProof(
            @Valid @RequestBody RegisterProofRequest request) {
        try {
            String txHash = blockchainService.registerProof(
                request.getDocHash(), 
                request.getDocType()
            );
            
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Proof registered successfully")
                    .data(txHash)
                    .build());
        } catch (Exception e) {
            log.error("Failed to register proof", e);
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to register proof: " + e.getMessage())
                    .build());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyProof(
            @Valid @RequestBody VerifyProofRequest request) {
        try {
            boolean exists = blockchainService.verifyOnChain(request.getDocHash());
            
            return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                    .success(true)
                    .message(exists ? "Proof found" : "Proof not found")
                    .data(exists)
                    .build());
        } catch (Exception e) {
            log.error("Failed to verify proof", e);
            return ResponseEntity.badRequest().body(ApiResponse.<Boolean>builder()
                    .success(false)
                    .message("Failed to verify proof: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/proof/{docHash}")
    public ResponseEntity<ApiResponse<ProofDetails>> getProofDetails(
            @PathVariable String docHash) {
        try {
            var details = blockchainService.getProofDetails(docHash);
            
            if (details.isPresent()) {
                return ResponseEntity.ok(ApiResponse.<ProofDetails>builder()
                        .success(true)
                        .message("Proof details retrieved")
                        .data(details.get())
                        .build());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Failed to get proof details", e);
            return ResponseEntity.badRequest().body(ApiResponse.<ProofDetails>builder()
                    .success(false)
                    .message("Failed to get proof details: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<BlockchainStatus>> getStatus() {
        try {
            boolean connected = blockchainService.isConnected();
            String balance = blockchainService.getWalletBalance();
            
            BlockchainStatus status = BlockchainStatus.builder()
                    .connected(connected)
                    .balance(balance)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.<BlockchainStatus>builder()
                    .success(true)
                    .message("Blockchain status retrieved")
                    .data(status)
                    .build());
        } catch (Exception e) {
            log.error("Failed to get blockchain status", e);
            return ResponseEntity.badRequest().body(ApiResponse.<BlockchainStatus>builder()
                    .success(false)
                    .message("Failed to get status: " + e.getMessage())
                    .build());
        }
    }

    @lombok.Data
    @lombok.Builder
    public static class BlockchainStatus {
        private boolean connected;
        private String balance;
    }
}
