package com.datavault.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;


import java.math.BigInteger;
import java.util.List;

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
        web3j = Web3j.build(new HttpService(rpcUrl));
        credentials = Credentials.create(privateKey);
        gasProvider = new DefaultGasProvider();

        contract = DocumentProofRegistry.load(
                contractAddress, web3j, credentials, gasProvider);
    }

    public String registerProof(String sha256, String docType) throws Exception {
        var tx = contract.registerProof(sha256, docType).send();
        return tx.getTransactionHash();
    }

    public boolean verifyOnChain(String sha256) throws Exception {
        var result = contract.verifyProof(sha256).send();
        Boolean exists = result.component1();
        return exists != null && exists;
    }
}
