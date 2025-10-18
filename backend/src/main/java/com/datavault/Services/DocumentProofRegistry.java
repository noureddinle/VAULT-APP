package com.datavault.Services;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class DocumentProofRegistry extends Contract {
    public static final String BINARY = "6080604052348015600e575f5ffd5b50610aa38061001c5f395ff3fe608060405234801561000f575f5ffd5b506004361061003f575f3560e01c80632640df7714610043578063447833441461006f57806354c71a4014610084575b5f5ffd5b610056610051366004610728565b6100a4565b6040516100669493929190610795565b60405180910390f35b61008261007d3660046107d1565b610285565b005b610097610092366004610728565b6104cf565b604051610066919061083d565b5f5f60605f5f5f87876040516100bb9291906108aa565b90815260408051918290036020908101832060a0840190925281546001600160a01b03168352600182018054918401916100f4906108b9565b80601f0160208091040260200160405190810160405280929190818152602001828054610120906108b9565b801561016b5780601f106101425761010080835404028352916020019161016b565b820191905f5260205f20905b81548152906001019060200180831161014e57829003601f168201915b50505050508152602001600282018054610184906108b9565b80601f01602080910402602001604051908101604052809291908181526020018280546101b0906108b9565b80156101fb5780601f106101d2576101008083540402835291602001916101fb565b820191905f5260205f20905b8154815290600101906020018083116101de57829003601f168201915b50505091835250506003820154602082015260049091015460ff16151560409091015260808101519091506102635760405162461bcd60e51b8152602060048201526009602482015268139bdd08199bdd5b9960ba1b60448201526064015b60405180910390fd5b6080810151815160408301516060909301519199909850919650945092505050565b826102bf5760405162461bcd60e51b815260206004820152600a60248201526908adae0e8f240d0c2e6d60b31b604482015260640161025a565b806102fd5760405162461bcd60e51b815260206004820152600e60248201526d456d70747920646f63207479706560901b604482015260640161025a565b5f848460405161030e9291906108aa565b9081526040519081900360200190206004015460ff16156103665760405162461bcd60e51b8152602060048201526012602482015271105b1c9958591e481c9959da5cdd195c995960721b604482015260640161025a565b6040518060a00160405280336001600160a01b0316815260200185858080601f0160208091040260200160405190810160405280939291908181526020018383808284375f92019190915250505090825250604080516020601f8601819004810282018101909252848152918101919085908590819084018382808284375f9201829052509385525050426020840152506001604092830152905161040e90879087906108aa565b90815260405160209181900382019020825181546001600160a01b0319166001600160a01b0390911617815590820151600182019061044d9082610951565b50604082015160028201906104629082610951565b50606082015160038201556080909101516004909101805460ff191691151591909117905560405133907f78113298a8a673a10ed09ead4c7d0530cc70d4e2f7fb64bcde585b5ae40f3627906104c19087908790879087904290610a34565b60405180910390a250505050565b6105096040518060a001604052805f6001600160a01b0316815260200160608152602001606081526020015f81526020015f151581525090565b5f838360405161051a9291906108aa565b9081526040519081900360200190206004015460ff166105685760405162461bcd60e51b8152602060048201526009602482015268139bdd08199bdd5b9960ba1b604482015260640161025a565b5f83836040516105799291906108aa565b90815260408051918290036020908101832060a0840190925281546001600160a01b03168352600182018054918401916105b2906108b9565b80601f01602080910402602001604051908101604052809291908181526020018280546105de906108b9565b80156106295780601f1061060057610100808354040283529160200191610629565b820191905f5260205f20905b81548152906001019060200180831161060c57829003601f168201915b50505050508152602001600282018054610642906108b9565b80601f016020809104026020016040519081016040528092919081815260200182805461066e906108b9565b80156106b95780601f10610690576101008083540402835291602001916106b9565b820191905f5260205f20905b81548152906001019060200180831161069c57829003601f168201915b50505091835250506003820154602082015260049091015460ff1615156040909101529392505050565b5f5f83601f8401126106f3575f5ffd5b50813567ffffffffffffffff81111561070a575f5ffd5b602083019150836020828501011115610721575f5ffd5b9250929050565b5f5f60208385031215610739575f5ffd5b823567ffffffffffffffff81111561074f575f5ffd5b61075b858286016106e3565b90969095509350505050565b5f81518084528060208401602086015e5f602082860101526020601f19601f83011685010191505092915050565b84151581526001600160a01b03841660208201526080604082018190525f906107c090830185610767565b905082606083015295945050505050565b5f5f5f5f604085870312156107e4575f5ffd5b843567ffffffffffffffff8111156107fa575f5ffd5b610806878288016106e3565b909550935050602085013567ffffffffffffffff811115610825575f5ffd5b610831878288016106e3565b95989497509550505050565b602080825282516001600160a01b03168282015282015160a060408301525f9061086a60c0840182610767565b90506040840151601f198483030160608501526108878282610767565b915050606084015160808401526080840151151560a08401528091505092915050565b818382375f9101908152919050565b600181811c908216806108cd57607f821691505b6020821081036108eb57634e487b7160e01b5f52602260045260245ffd5b50919050565b634e487b7160e01b5f52604160045260245ffd5b601f82111561094c57805f5260205f20601f840160051c8101602085101561092a5750805b601f840160051c820191505b81811015610949575f8155600101610936565b50505b505050565b815167ffffffffffffffff81111561096b5761096b6108f1565b61097f8161097984546108b9565b84610905565b6020601f8211600181146109b1575f831561099a5750848201515b5f19600385901b1c1916600184901b178455610949565b5f84815260208120601f198516915b828110156109e057878501518255602094850194600190920191016109c0565b50848210156109fd57868401515f19600387901b60f8161c191681555b50505050600190811b01905550565b81835281816020850137505f828201602090810191909152601f909101601f19169091010190565b606081525f610a47606083018789610a0c565b8281036020840152610a5a818688610a0c565b915050826040830152969550505050505056fea264697066735822122055ef529e05fe246dcc3a527c35cc5d751492f1aec30102b7be8a02804a63dec464736f6c634300081e0033";

    private static String librariesLinkedBinary;

    public static final String FUNC_GETPROOFDETAILS = "getProofDetails";

    public static final String FUNC_REGISTERPROOF = "registerProof";

    public static final String FUNC_VERIFYPROOF = "verifyProof";

    public static final Event PROOFREGISTERED_EVENT = new Event("ProofRegistered", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected DocumentProofRegistry(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected DocumentProofRegistry(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected DocumentProofRegistry(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected DocumentProofRegistry(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<ProofRegisteredEventResponse> getProofRegisteredEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PROOFREGISTERED_EVENT, transactionReceipt);
        ArrayList<ProofRegisteredEventResponse> responses = new ArrayList<ProofRegisteredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ProofRegisteredEventResponse typedResponse = new ProofRegisteredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.docHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.docType = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static ProofRegisteredEventResponse getProofRegisteredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PROOFREGISTERED_EVENT, log);
        ProofRegisteredEventResponse typedResponse = new ProofRegisteredEventResponse();
        typedResponse.log = log;
        typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.docHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.docType = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<ProofRegisteredEventResponse> proofRegisteredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getProofRegisteredEventFromLog(log));
    }

    public Flowable<ProofRegisteredEventResponse> proofRegisteredEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROOFREGISTERED_EVENT));
        return proofRegisteredEventFlowable(filter);
    }

    public RemoteFunctionCall<Proof> getProofDetails(String docHash) {
        final Function function = new Function(FUNC_GETPROOFDETAILS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(docHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Proof>() {}));
        return executeRemoteCallSingleValueReturn(function, Proof.class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerProof(String docHash, String docType) {
        final Function function = new Function(
                FUNC_REGISTERPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(docHash), 
                new org.web3j.abi.datatypes.Utf8String(docType)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple4<Boolean, String, String, BigInteger>> verifyProof(
            String docHash) {
        final Function function = new Function(FUNC_VERIFYPROOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(docHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple4<Boolean, String, String, BigInteger>>(function,
                new Callable<Tuple4<Boolean, String, String, BigInteger>>() {
                    @Override
                    public Tuple4<Boolean, String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<Boolean, String, String, BigInteger>(
                                (Boolean) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    @Deprecated
    public static DocumentProofRegistry load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new DocumentProofRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static DocumentProofRegistry load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new DocumentProofRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static DocumentProofRegistry load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new DocumentProofRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static DocumentProofRegistry load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new DocumentProofRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<DocumentProofRegistry> deploy(Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DocumentProofRegistry.class, web3j, credentials, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<DocumentProofRegistry> deploy(Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DocumentProofRegistry.class, web3j, credentials, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    public static RemoteCall<DocumentProofRegistry> deploy(Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(DocumentProofRegistry.class, web3j, transactionManager, contractGasProvider, getDeploymentBinary(), "");
    }

    @Deprecated
    public static RemoteCall<DocumentProofRegistry> deploy(Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(DocumentProofRegistry.class, web3j, transactionManager, gasPrice, gasLimit, getDeploymentBinary(), "");
    }

    private static String getDeploymentBinary() {
        if (librariesLinkedBinary != null) {
            return librariesLinkedBinary;
        } else {
            return BINARY;
        }
    }

    public static class Proof extends DynamicStruct {
        public String owner;

        public String docHash;

        public String docType;

        public BigInteger timestamp;

        public Boolean exists;

        public Proof(String owner, String docHash, String docType, BigInteger timestamp,
                Boolean exists) {
            super(new org.web3j.abi.datatypes.Address(160, owner), 
                    new org.web3j.abi.datatypes.Utf8String(docHash), 
                    new org.web3j.abi.datatypes.Utf8String(docType), 
                    new org.web3j.abi.datatypes.generated.Uint256(timestamp), 
                    new org.web3j.abi.datatypes.Bool(exists));
            this.owner = owner;
            this.docHash = docHash;
            this.docType = docType;
            this.timestamp = timestamp;
            this.exists = exists;
        }

        public Proof(Address owner, Utf8String docHash, Utf8String docType, Uint256 timestamp,
                Bool exists) {
            super(owner, docHash, docType, timestamp, exists);
            this.owner = owner.getValue();
            this.docHash = docHash.getValue();
            this.docType = docType.getValue();
            this.timestamp = timestamp.getValue();
            this.exists = exists.getValue();
        }
    }

    public static class ProofRegisteredEventResponse extends BaseEventResponse {
        public String owner;

        public String docHash;

        public String docType;

        public BigInteger timestamp;
    }
}
