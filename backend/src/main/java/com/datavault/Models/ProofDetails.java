package com.datavault.Models;

import java.math.BigInteger;
import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProofDetails {
    private Boolean exists;
    private String owner;
    private String docType;
    private BigInteger timestamp;
    private Instant registeredAt;
}