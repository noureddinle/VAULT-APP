package com.datavault.Services;

import org.springframework.stereotype.Service;
import com.datavault.Repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentVerificationService {

    private final DocumentRepository documentRepository;
    private final BlockchainService blockchainService;

    public VerificationResult verifyDocument(String sha256) throws Exception {
        var dbDoc = documentRepository.findBySha256(sha256);
        boolean dbExists = dbDoc.isPresent();

        boolean blockchainExists = blockchainService.verifyOnChain(sha256);

        return new VerificationResult(
                dbExists,
                blockchainExists,
                dbExists ? dbDoc.get().getBlockchainTx() : null,
                dbExists ? dbDoc.get().getDocType() : null
        );
    }

    public record VerificationResult(
            boolean databaseRecord,
            boolean blockchainRecord,
            String blockchainTx,
            String docType
    ) {}
}
