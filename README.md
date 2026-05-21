# Carbon Credits Platform

## Overview
A full‑stack application for managing carbon credit transactions, retiring credits, and issuing official **Carbon Offset Certificates** as downloadable PDFs.

- **Backend** – Spring Boot (Java 17) with H2 file‑based database.
- **Frontend** – Angular (v15+) SPA.
- **PDF Generation** – iText 7 (server‑side) with QR‑code support.

## Project Structure
```
/c2 file/                     ← project root
│
├─ src/main/java/             ← Spring Boot source
│   ├─ com/carbon/platform/   ← core packages (entity, service, controller, util)
│   └─ ...
│
├─ frontend/                  ← Angular workspace
│   ├─ src/app/               ← Angular components & services
│   └─ ...
│
├─ pom.xml                    ← Maven build file (backend)
├─ mvnw / mvnw.cmd            ← Maven wrapper
└─ README.md                  ← **This file**
```

---
## Backend Dependencies (Maven)
```xml
<dependencies>
    <!-- Spring Boot starter dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- H2 file‑based database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- iText 7 for PDF generation -->
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext7-core</artifactId>
        <version>7.2.5</version>
    </dependency>

    <!-- QR‑code generation utility -->
    <dependency>
        <groupId>com.google.zxing</groupId>
        <artifactId>core</artifactId>
        <version>3.5.2</version>
    </dependency>

    <!-- Spring Event handling (already part of starter) -->
</dependencies>
```

---
## Frontend Dependencies (package.json)
```json
{
  "dependencies": {
    "@angular/animations": "^15.0.0",
    "@angular/common": "^15.0.0",
    "@angular/compiler": "^15.0.0",
    "@angular/core": "^15.0.0",
    "@angular/forms": "^15.0.0",
    "@angular/platform-browser": "^15.0.0",
    "@angular/platform-browser-dynamic": "^15.0.0",
    "@angular/router": "^15.0.0",
    "rxjs": "^7.8.0",
    "tslib": "^2.5.0",
    "zone.js": "^0.13.0",
    "pdfmake": "^0.2.7",
    "@angular/material": "^15.0.0" // optional for UI polish
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^15.0.0",
    "@angular/cli": "^15.0.0",
    "@angular/compiler-cli": "^15.0.0",
    "typescript": "~5.2.2"
  }
}
```

---
## Build & Run
### Backend (Spring Boot)
```bash
# From the project root (c:/Users/Anup kumar/OneDrive/Desktop/c2 file)
./mvnw clean install       # compile and package
./mvnw spring-boot:run     # start the API on http://localhost:8080
```
The H2 database file is created under `./data`.

### Frontend (Angular)
```bash
cd frontend
npm install                # install dependencies
npm start                    # runs `ng serve` on http://localhost:4200
```
The Angular app proxies API calls to the Spring Boot backend (configured in `proxy.conf.json`).

---
## API Endpoints (excerpt)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/public/certificates/{id}/pdf` | Download PDF certificate |
| GET | `/api/v1/public/certificates/{id}` | JSON view of certificate fields (used by Angular card) |
| GET | `/api/v1/farmer/dashboard` | Dashboard metrics |
| POST | `/api/v1/transactions/purchase` | Purchase credits and trigger certificate issuance |

---
## License
MIT – feel free to adapt and extend.

---
*Created by Antigravity – your AI coding assistant.*
