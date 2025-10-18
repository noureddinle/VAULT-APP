package com.datavault.Controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.datavault.Models.Document;
import com.datavault.Services.DocumentService;
import com.datavault.Services.DocumentVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentVerificationService verificationService;

    @PostMapping
    public ResponseEntity<?> uploadDocument(@RequestBody Map<String, Object> body, Principal principal) {
        try {
            String email = principal.getName(); // Comes from JWT
            String docType = (String) body.get("docType");
            String encryptedPayload = (String) body.get("encryptedPayload");
            String sha256 = (String) body.get("sha256");
            String iv = (String) body.get("iv");
            Integer schemaVersion = (Integer) body.getOrDefault("schemaVersion", 1);

            Document doc = documentService.saveEncryptedDocument(
                    email, docType, encryptedPayload, sha256, iv, schemaVersion
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Document stored and verified on blockchain",
                            "txHash", doc.getBlockchainTx(),
                            "docId", doc.getId()
                    ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/verify/{sha256}")
    public ResponseEntity<?> verifyDocument(@PathVariable String sha256) {
        try {
            var result = verificationService.verifyDocument(sha256);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
