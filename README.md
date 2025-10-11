# 📁 Media Service — Sumar+

## Project Description

The **Media Service** is responsible for **file storage and retrieval** across the Sumar+ platform.  
It provides endpoints for uploading files, validating integrity via hash functions, and retrieving them by their unique **UUID**.

This microservice is consumed by other through the **API Gateway**.

---

## ⚙️ Service Information

| Property | Value |
|-----------|--------|
| **Service name** | media-service |
| **Port** | `8083` |
| **Base path** | `/api/v1/media` |
| **Technology stack** | Java 17 · Spring Boot · MySQL 8 · Apache Tika |

---

## 🧩 Endpoints Overview

### 1. **Upload File**
`POST /api/v1/media/files`

Uploads a file to the system and checks for duplicates using its **SHA-256 hash**.  
If the file already exists, returns the existing **UUID**, avoiding duplicates.  
If it does not exist, the file is saved both in the **file system** and in the **metadata database**.

#### 🧠 Notes
- File content is stored in a **Docker volume**.
- The database stores:
    - `id` (UUID)
    - `name` (original filename)
    - `hashSha256` (SHA-256)
    - `path` (filesystem path)
    - `mimeType` (MIME type detected via Apache Tika)
    - `extension`
    - `creationDate`

#### 🔹 Example Request
```bash
curl -X POST "http://localhost:8083/api/v1/media/files"   -H "accept: */*"   -H "Content-Type: multipart/form-data"   -F "file=@example.txt;type=text/plain"   -F "hashMd5="   -F "hashSha256="
```

#### 🔹 Example Response
```json
{
  "uuid": "c922ccbf-fbdc-4fc7-b22b-26f4193ee156"
}
```

---

### 2. **Retrieve File (Binary)**
`GET /api/v1/media/files/{uuid}`

Retrieves a file from the system by its **UUID**.  
The file is returned as a **binary stream** in the response body, and its metadata is included in the HTTP headers.

#### 🔹 Example Request
```bash
curl --location "http://localhost:8083/api/v1/media/files/{uuid}" --output output.txt
```

#### 🔹 Example Response
**Headers:**
```
X-File-Id: c922ccbf-fbdc-4fc7-b22b-26f4193ee156
X-File-Name: example.txt
X-File-Mime: text/plain
X-File-Ext: txt
X-File-Size: 1024
```

**Body:**
- Binary file stream (`byte[]`)

#### 🔹 Possible Status Codes
| Code | Description |
|------|--------------|
| 200 | File retrieved successfully |
| 404 | File not found |
| 500 | Internal server error |

---

### 3. **Retrieve File (Base64)**
`GET /api/v1/media/files/{uuid}/base64`

Retrieves the same file but encoded in **Base64**, allowing transmission as JSON.

#### 🔹 Example Request
```bash
curl --location "http://localhost:8083/api/v1/media/files/{uuid}/base64"
```

#### 🔹 Example Response
```json
{
  "uuid": "c922ccbf-fbdc-4fc7-b22b-26f4193ee156",
  "filename": "example.txt",
  "mimetype": "text/plain",
  "extension": "txt",
  "sha256": "29c82c870fc94142d1361f088528c8f9fe3a9bb186345c1f701a57683c9a4707",
  "base64": "SGVsbG8gd29ybGQ="
}
```

---

## 🧠 Error Response Format

All error responses follow a common JSON structure.

#### 🔹 Example
```json
{
  "timestamp": "2025-10-11T10:32:18.124",
  "status": 404,
  "error": "Not Found",
  "message": "File not found"
}
```

#### Fields:
| Field | Description |
|--------|--------------|
| `timestamp` | Date and time of the error |
| `status` | HTTP status code |
| `error` | Error type (e.g., `Bad Request`, `Not Found`) |
| `message` | Human-readable error description |

---

## 🧱 Technologies Used

- **Java 17**
- **Spring Boot 3.x**
- **MySQL 8.0**
- **Apache Tika** — MIME type detection
- **ModelMapper** — DTO/entity mapping
- **Lombok** — Boilerplate reduction
- **JUnit 5 + Mockito** — Testing


---

## 🔍 Observability (optional future improvements)

- Implement **ETag / If-None-Match** for efficient caching.
- Stream large files instead of loading them fully in memory.

---

© 2025 — *Sumar+ Platform · Universidad Tecnológica Nacional (UTN FRC)*
