package com.datavault.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datavault.Models.Document;
import com.datavault.Models.User;
import com.datavault.Repositories.DocumentRepository;
import com.datavault.Repositories.UserRepository;
import com.datavault.Services.BlockchainService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final BlockchainService blockchainService;

    @Transactional
    public Document saveEncryptedDocument(String email, String docType, String encryptedPayload, String sha256, String iv, Integer schemaVersion) throws Exception {
        User owner = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        if (documentRepository.findBySha256(sha256).isPresent()) {
            throw new RuntimeException("Document already exists on-chain");
        }

        Document doc = new Document();
        doc.setDocType(docType);
        doc.setEncryptedPayload(encryptedPayload);
        doc.setSha256(sha256);
        doc.setIv(iv);
        doc.setSchemaVersion(schemaVersion);
        doc.setOwner(owner);

        // Step 1: Save locally
        documentRepository.save(doc);

        // Step 2: Push hash to blockchain
        String txHash = blockchainService.registerProof(sha256, docType);
        doc.setBlockchainTx(txHash);

        // Step 3: Update record with transaction hash
        return documentRepository.save(doc);
    }
}
