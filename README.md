# 🧠 Vault-App — Secure Document Verification Platform

**Vault-App** is a full-stack system that combines **AI document parsing**, **cryptographic hashing**, and **blockchain verification** to securely manage and validate digital documents.

It uses:

* 🧩 **Spring Boot (Java)** for the backend API
* 🔗 **Polygon / Hardhat / Solidity** for blockchain document proof
* 🤖 **FastAPI + Transformers** for text parsing & embedding
* 📱 **Flutter** for the mobile interface
* 🔒 **AES encryption + SHA-256 hashing** for local and network security

---

## 📚 Table of Contents

1. [Architecture Overview](#-architecture-overview)
2. [Core Features](#-core-features)
3. [Tech Stack](#-tech-stack)
4. [Folder Structure](#-folder-structure)
5. [Setup Instructions](#-setup-instructions)
6. [Environment Variables](#-environment-variables)
7. [Blockchain Deployment](#️-blockchain-deployment)
8. [AI Parser & Embedding Service](#-ai-parser--embedding-service)
9. [API Endpoints](#-api-endpoints)
10. [Security Model](#-security-model)
11. [Future Enhancements](#-future-enhancements)

---

## 🏗 Architecture Overview

```
[ Flutter App ]
     ↓
[ FastAPI AI Parser ]  →  Extracts text & metadata
     ↓
[ Embedding Service ]  →  Converts text → semantic vector
     ↓
[ Spring Boot Backend ] →  Hash + Encrypt + Store
     ↓
[ Polygon Blockchain ] →  Registers immutable proof
     ↓
[ PostgreSQL / Vault DB ]
```

---

## 🚀 Core Features

✅ Parse and extract document details (ID, Passport, Invoice…)
✅ Generate secure **SHA-256** fingerprints
✅ Encrypt documents using AES before storage
✅ Verify documents on-chain via **Polygon** smart contracts
✅ Compare text similarity using AI embeddings
✅ Expose RESTful APIs for clients and third-party integrations

---

## ⚙ Tech Stack

| Layer           | Technology                                              |
| --------------- | ------------------------------------------------------- |
| **Frontend**    | Flutter, Dart                                           |
| **Backend API** | Spring Boot 3 (Java 17)                                 |
| **Database**    | PostgreSQL                                              |
| **Blockchain**  | Solidity, Hardhat, Polygon Amoy Testnet                 |
| **AI Services** | Python 3.11, FastAPI, Transformers, SentenceTransformer |
| **Security**    | AES-256-GCM, SHA-256, JWT                               |
| **Deployment**  | Docker, Vercel (for Flutter web), Railway/Hostinger VPS |

---

## 📂 Folder Structure

```
VAULT-APP/
│
├── backend/                     # Spring Boot backend
│   ├── src/main/java/com/datavault/
│   │   ├── Controllers/         # REST endpoints
│   │   ├── Services/            # Blockchain & Encryption logic
│   │   ├── Models/              # Entity models
│   │   ├── Dto/                 # Request/Response DTOs
│   │   └── utils/               # Hashing, encryption utilities
│   └── pom.xml
│
├── blockchain/                  # Hardhat Solidity project
│   ├── contracts/DocumentProofRegistry.sol
│   ├── scripts/deploy.js
│   ├── hardhat.config.ts
│   └── .env
│
├── ai_parser/                   # FastAPI OCR/Parsing service
│   └── main.py
│
├── embedding_service/           # FastAPI embedding generator
│   └── main.py
│
└── mobile_flutter/              # Flutter mobile app
    ├── lib/
    └── pubspec.yaml
```

---

## 🧰 Setup Instructions

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/yourusername/vault-app.git
cd vault-app
```

### 2️⃣ Backend Setup (Spring Boot)

```bash
cd backend
./mvnw clean install
```

Run the server:

```bash
./mvnw spring-boot:run
```

### 3️⃣ Blockchain Setup (Polygon via Hardhat)

```bash
cd blockchain
npm install
npx hardhat compile
```

Deploy to Polygon:

```bash
npx hardhat run scripts/deploy.js --network polygonAmoy
```

### 4️⃣ AI Parser & Embedding Service

#### AI Parser (Hugging Face Transformers)

```bash
cd ai_parser
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

#### Embedding Service

```bash
cd embedding_service
pip install -r requirements.txt
uvicorn main:app --reload --port 8001
```

### 5️⃣ Flutter Mobile App

```bash
cd mobile_flutter
flutter pub get
flutter run
```

---

## 🔑 Environment Variables

Create `.env` files where needed:

### `backend/.env`

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/vaultdb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=yourpassword

POLYGON_RPC=https://rpc-amoy.polygon.technology
POLYGON_PRIVATE_KEY=your_wallet_private_key
POLYGON_CONTRACT_ADDRESS=your_deployed_contract_address
```

### `blockchain/.env`

```env
POLYGON_RPC_URL=https://rpc-amoy.polygon.technology
POLYGON_PRIVATE_KEY=your_wallet_private_key
POLYGONSCAN_API_KEY=your_polygonscan_key
```

---

## ⛓️ Blockchain Deployment

### Compile and Deploy

```bash
npx hardhat compile
npx hardhat run scripts/deploy.js --network polygonAmoy
```

### Verify Contract

```bash
npx hardhat verify --network polygonAmoy DEPLOYED_CONTRACT_ADDRESS
```

### Example Output

```
✅ Contract deployed to: 0x1234abcd5678ef9012345678ef9012345678abcd
```

---

## 🤖 AI Parser & Embedding Service

**AI Parser (FastAPI + Transformers)**
Extracts and structures document details (OCR, MRZ, PDF417).

**Embedding Service (SentenceTransformer)**
Generates semantic vectors for document text.
Optional `/compare` endpoint can measure similarity between documents.

Example request:

```bash
curl -X POST http://localhost:8001/embed -H "Content-Type: application/json" \
-d '{"text":"Passport John Doe Morocco"}'
```

Response:

```json
{"embedding":[0.134, -0.292, 0.880, ...]}
```

---

## 📡 API Endpoints (Backend)

| Method | Endpoint                       | Description                        |
| ------ | ------------------------------ | ---------------------------------- |
| `POST` | `/api/blockchain/register`     | Register document proof            |
| `POST` | `/api/blockchain/verify`       | Verify proof existence             |
| `GET`  | `/api/blockchain/proof/{hash}` | Get proof details                  |
| `GET`  | `/api/blockchain/status`       | Check blockchain connectivity      |
| `POST` | `/api/parse` *(planned)*       | Parse document and extract details |

---

## 🔒 Security Model

| Layer          | Protection                                   |
| -------------- | -------------------------------------------- |
| File Parsing   | Local-only AI service                        |
| Hashing        | SHA-256 fingerprinting                       |
| Encryption     | AES-256-GCM via `EncryptionService`          |
| Authentication | JWT for user access                          |
| Blockchain     | Immutable proof registration                 |
| Database       | Encrypted data at rest (PostgreSQL + Flyway) |

---

## 🧭 Future Enhancements

* 🧩 Integrate Homomorphic Encryption for encrypted similarity comparison
* 🕵️ Add Zero-Knowledge Proof (ZKP) for blockchain privacy layer
* 🌍 Add IPFS decentralized file storage
* 🧠 Integrate multilingual OCR (Arabic, French, English)
* 💬 Add document AI chat (retrieval-augmented QA over vault data)

---

## 🧑‍💻 Author

**Othman Essaadi**
💼 Full-stack & AI Engineer
🔗 [GitHub](https://github.com/yourusername) • [LinkedIn](https://linkedin.com/in/yourprofile)

---

> ⚡ “Vault-App — Where AI, Blockchain, and Privacy converge.”
