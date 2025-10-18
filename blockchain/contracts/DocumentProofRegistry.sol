pragma solidity ^0.8.20;

contract DocumentProofRegistry {
    struct Proof {
        address owner;
        string docHash;
        string docType;
        uint256 timestamp;
        bool exists;
    }

    mapping(string => Proof) private proofs;

    event ProofRegistered(
        address indexed owner, 
        string docHash, 
        string docType,
        uint256 timestamp
    );

    function registerProof(string calldata docHash, string calldata docType) external {
        require(bytes(docHash).length > 0, "Empty hash");
        require(bytes(docType).length > 0, "Empty doc type");
        require(!proofs[docHash].exists, "Already registered");

        proofs[docHash] = Proof({
            owner: msg.sender,
            docHash: docHash,
            docType: docType,
            timestamp: block.timestamp,
            exists: true
        });

        emit ProofRegistered(msg.sender, docHash, docType, block.timestamp);
    }

    function verifyProof(string calldata docHash)
        external
        view
        returns (
            bool exists,
            address owner,
            string memory docType,
            uint256 timestamp
        )
    {
        Proof memory p = proofs[docHash];
        require(p.exists, "Not found");
        return (p.exists, p.owner, p.docType, p.timestamp);
    }

    function getProofDetails(string calldata docHash)
        external
        view
        returns (Proof memory)
    {
        require(proofs[docHash].exists, "Not found");
        return proofs[docHash];
    }
}