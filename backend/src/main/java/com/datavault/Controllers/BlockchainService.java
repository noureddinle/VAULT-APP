package com.datavault.Controllers;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;

import com.datavault.Models.ProofDetails;
import com.datavault.Services.DocumentProofRegistry;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BlockchainService {

    @Value("${polygon.rpc}")
    private String rpcUrl;

    @Value("${polygon.private-key}")
    private String privateKey;

    @Value("${polygon.contract-address}")
    private String contractAddress;

    private Web3j web3j;
    private Credentials credentials;
    private ContractGasProvider gasProvider;
    private DocumentProofRegistry contract;

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing blockchain service...");
            web3j = Web3j.build(new HttpService(rpcUrl));
            credentials = Credentials.create(privateKey);
            gasProvider = new DefaultGasProvider();

            contract = DocumentProofRegistry.load(
                    contractAddress, web3j, credentials, gasProvider);
            
            log.info("Blockchain service initialized successfully");
            log.info("Contract address: {}", contractAddress);
            log.info("Wallet address: {}", credentials.getAddress());
        } catch (Exception e) {
            log.error("Failed to initialize blockchain service", e);
            throw new RuntimeException("Blockchain initialization failed", e);
        }
    }

    /**
     * Register a document proof on the blockchain
     * @param sha256 The SHA-256 hash of the document
     * @param docType The type of document (e.g., "PDF", "DOCX", "IMAGE")
     * @return Transaction hash
     */
    public String registerProof(String sha256, String docType) throws Exception {
        log.info("Registering proof - Hash: {}, Type: {}", sha256, docType);
        
        try {
            TransactionReceipt receipt = contract.registerProof(sha256, docType).send();
            String txHash = receipt.getTransactionHash();
            
            log.info("Proof registered successfully - TxHash: {}", txHash);
            return txHash;
        } catch (Exception e) {
            log.error("Failed to register proof", e);
            throw new Exception("Failed to register proof: " + e.getMessage(), e);
        }
    }

    /**
     * Verify if a document exists on the blockchain
     * @param sha256 The SHA-256 hash to verify
     * @return true if exists, false otherwise
     */
    public boolean verifyOnChain(String sha256) throws Exception {
        log.info("Verifying proof - Hash: {}", sha256);
        
        try {
            var result = contract.verifyProof(sha256).send();
            Boolean exists = result.component1();
            
            log.info("Verification result for {}: {}", sha256, exists);
            return exists != null && exists;
        } catch (Exception e) {
            // If "Not found" error, return false instead of throwing
            if (e.getMessage().contains("Not found")) {
                log.info("Proof not found for hash: {}", sha256);
                return false;
            }
            log.error("Failed to verify proof", e);
            throw new Exception("Failed to verify proof: " + e.getMessage(), e);
        }
    }

    /**
     * Get detailed information about a registered proof
     * @param sha256 The SHA-256 hash
     * @return ProofDetails object with all information
     */
    public Optional<ProofDetails> getProofDetails(String sha256) throws Exception {
        log.info("Getting proof details - Hash: {}", sha256);
        
        try {
            var result = contract.verifyProof(sha256).send();
            
            ProofDetails details = ProofDetails.builder()
                    .exists(result.component1())
                    .owner(result.component2())
                    .docType(result.component3())
                    .timestamp(result.component4())
                    .registeredAt(Instant.ofEpochSecond(result.component4().longValue()))
                    .build();
            
            log.info("Proof details retrieved: {}", details);
            return Optional.of(details);
        } catch (Exception e) {
            if (e.getMessage().contains("Not found")) {
                log.info("Proof not found for hash: {}", sha256);
                return Optional.empty();
            }
            log.error("Failed to get proof details", e);
            throw new Exception("Failed to get proof details: " + e.getMessage(), e);
        }
    }

    /**
     * Check if the service is connected to the blockchain
     * @return true if connected
     */
    public boolean isConnected() {
        try {
            web3j.web3ClientVersion().send();
            return true;
        } catch (Exception e) {
            log.error("Blockchain connection check failed", e);
            return false;
        }
    }

    /**
     * Get the current wallet balance
     * @return Balance in ETH/MATIC
     */
    public String getWalletBalance() throws Exception {
        try {
            BigInteger balance = web3j.ethGetBalance(
                credentials.getAddress(),
                org.web3j.protocol.core.DefaultBlockParameterName.LATEST
            ).send().getBalance();
            
            return org.web3j.utils.Convert.fromWei(
                balance.toString(), 
                org.web3j.utils.Convert.Unit.ETHER
            ).toString();
        } catch (Exception e) {
            log.error("Failed to get wallet balance", e);
            throw new Exception("Failed to get wallet balance: " + e.getMessage(), e);
        }
    }
}