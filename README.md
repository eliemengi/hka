# Hochschule Microservice

A Spring Boot REST microservice for managing universities (Hochschulen), built as part of the **Wirtschaftsinformatik** course at **Hochschule Karlsruhe (HKA)** under Prof. Dr. Jürgen Zimmermann.

---

## Overview

This project implements a production-ready REST API for managing university entities including their addresses and faculties. It demonstrates modern Java microservice development practices including containerization, Kubernetes deployment, comprehensive testing, and code quality analysis.

The service exposes endpoints to **create**, **read**, **search**, and **update** university records, backed by an in-memory repository. The entire stack runs locally via Docker Desktop's built-in Kubernetes cluster, deployed through Helm charts orchestrated by Terraform.

---

## Tech Stack

### Core
- **Java 26** (Azul Zulu) with preview features enabled
- **Spring Boot 4.1.0-M4** (Milestone release)
- **Spring Framework 7.0.6**
- **Maven** as build tool

### Runtime & Deployment
- **Docker Desktop** with built-in Kubernetes
- **Paketo Buildpacks** (Bellsoft Liberica) for OCI image creation
- **Helm 3** for Kubernetes package management
- **Terraform** for infrastructure orchestration

### Testing & Quality
- **JUnit 5** (Jupiter) with `@Nested` test classes
- **AssertJ** with SoftAssertions
- **Mockito** for mocking
- **JaCoCo** for code coverage
- **Checkstyle**, **PMD**, **SpotBugs** for static analysis
- **OWASP Dependency Check** for vulnerability scanning

### API Testing
- **Bruno** as offline API client

---

## Project Structure

```
hka/
├── src/
│   ├── main/
│   │   ├── java/com/example/hka/
│   │   │   ├── HkaApplication.java
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   │   ├── HochschuleController.java
│   │   │   │   ├── HochschuleDTO.java
│   │   │   │   ├── HochschuleMapper.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── entity/
│   │   │   │   ├── Hochschule.java
│   │   │   │   ├── Adresse.java
│   │   │   │   └── Fakultaet.java
│   │   │   ├── repository/
│   │   │   │   └── HochschuleRepository.java
│   │   │   └── service/
│   │   │       ├── HochschuleService.java
│   │   │       └── NotFoundException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── certificate.crt
│   │       └── private-key.pem
│   └── test/
│       └── java/com/example/hka/controller/
│           └── HochschuleControllerTest.java
├── extras/
│   ├── helm/hka/              # Helm chart
│   ├── terraform/             # Terraform configuration
│   ├── config/                # Checkstyle, PMD, SpotBugs rules
│   └── port-forward.ps1
├── pom.xml
└── Dockerfile
```

---

## Domain Model

The service models three core entities:

### `Hochschule` (University)
The aggregate root containing:
- `id` — UUID identifier
- `name` — University name
- `adresse` — One-to-one relationship with `Adresse`
- `fakultaet` — One-to-many relationship with `Fakultaet` (list)

### `Adresse` (Address)
- `strasse` — Street
- `ort` — City

### `Fakultaet` (Faculty)
- `name` — Faculty name
- `studentenAnzahl` — Number of students

---

## REST API

All endpoints are served over **HTTPS** on port **8443** with self-signed certificates.

| Method | Endpoint | Description | Success | Error |
|--------|----------|-------------|---------|-------|
| `GET` | `/hochschulen` | List all universities | `200 OK` | — |
| `GET` | `/hochschulen/{id}` | Get university by UUID | `200 OK` | `404 Not Found` |
| `GET` | `/hochschulen/search?name={name}` | Search by name (contains match) | `200 OK` | — |
| `POST` | `/hochschulen` | Create new university | `201 Created` | `400 Bad Request` |
| `PUT` | `/hochschulen/{id}` | Update existing university | `200 OK` | `404 Not Found`, `400 Bad Request` |

### Example Request Body (POST/PUT)

```json
{
  "name": "Hochschule Karlsruhe",
  "strasse": "Moltkestrasse 30",
  "ort": "Karlsruhe",
  "fakultaeten": ["Informatik", "Maschinenbau"]
}
```

### Validation

All input is validated using Jakarta Bean Validation:
- `name`, `strasse`, `ort` — `@NotBlank`
- `fakultaeten` — `@NotEmpty`

Invalid requests return `400 Bad Request` handled by `GlobalExceptionHandler`.

---

## Getting Started

### Prerequisites

- **Java 26** (e.g. Azul Zulu)
- **Maven 3.9+**
- **Docker Desktop** with Kubernetes enabled
- **Terraform 1.15.0**
- **Helm 3**
- **kubectl**

### Local Development

**Run the server:**
```powershell
cd path\to\hka
mvn spring-boot:run
```

The server starts on `https://localhost:8443`.

**Test endpoints with curl:**
```powershell
# Get all universities (requires --insecure for self-signed cert)
curl --insecure https://localhost:8443/hochschulen

# Get by ID
curl --insecure https://localhost:8443/hochschulen/{uuid}

# Search by name
curl --insecure "https://localhost:8443/hochschulen/search?name=Karlsruhe"
```

---

## Testing

The test suite follows the Zimmermann standard with `@Nested` classes organized by behavior:

```
HochschuleControllerTest
├── FindById          # Search by ID
├── FindByName        # Search with parameters
└── Create            # Create new entity
```

**Run all tests with coverage report:**

```powershell
mvn test jacoco:report
```

Coverage report opens at `target/site/jacoco/index.html`.

### Test Approach

Tests use **`@SpringBootTest(webEnvironment = RANDOM_PORT)`** for full integration testing, executing real HTTP requests against the running application via `RestClient` configured with the SSL bundle.

Key annotations:
- `@Tag("rest")` — Test categorization
- `@ActiveProfiles("dev")` — Use dev profile
- `@ExtendWith(SoftAssertionsExtension.class)` — Multiple assertions per test
- `@ParameterizedTest` with `@ValueSource` — Data-driven tests

---

## Build & Reports

**Generate full project site with all quality reports:**

```powershell
mvn site -D'maven.test.skip=true' -D'maven.javadoc.skip=true'
```

This produces reports under `target/site/`:

- **`project-reports.html`** — Overview of all reports
- **`surefire.html`** — Test results
- **`jacoco/index.html`** — Code coverage
- **`checkstyle.html`** — Style violations
- **`pmd.html`** — PMD analysis
- **`spotbugs.html`** — SpotBugs findings
- **`dependency-check-report.html`** — OWASP vulnerability scan

> **Note:** First run downloads ~130k CVE records from the NVD database. Subsequent runs use a local cache.

---

## Containerization

**Build an OCI image with Paketo Buildpacks (no Dockerfile needed):**

```powershell
mvn spring-boot:build-image -D'maven.test.skip=true'
```

This produces an image tagged with the project version and Paketo's Bellsoft Liberica runtime.

The image uses **Bellsoft Liberica JRE 26** on **Ubuntu Noble** with Native Memory Tracking enabled and a thread count optimized for microservices.

---

## Kubernetes Deployment

The service deploys to the local Docker Desktop Kubernetes cluster in namespace **`acme`**.

### Required Cluster Resources

Before deploying, ensure these one-time setup steps are completed:

**1. Create `medium-priority` PriorityClass:**
```powershell
@"
apiVersion: scheduling.k8s.io/v1
kind: PriorityClass
metadata:
  name: medium-priority
value: 1000000
globalDefault: false
description: Medium priority
"@ | kubectl apply -f -
```

**2. Create log directory on host:**
```powershell
New-Item -ItemType Directory -Force -Path "C:\path\to\volumes\hka-v1"
```

> Adjust the volume path in `extras/helm/hka/templates/deployment.yaml` to match your local directory.

### Deploy with Terraform

```powershell
cd extras\terraform
terraform init -upgrade
terraform apply -auto-approve
```

### Deploy with Helm (alternative)

```powershell
helm install hka extras\helm\hka `
    --namespace acme `
    --create-namespace `
    -f extras\terraform\dev\hka.yaml
```

### Verify Deployment

```powershell
kubectl get pods -n acme -w
```

Wait for status `1/1 Running`.

### Port Forwarding

To access the service from outside the cluster:

```powershell
cd extras
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
.\port-forward.ps1 8443 hka
```

### Cleanup

```powershell
terraform destroy -auto-approve
# or
helm uninstall hka --namespace acme
```

---

## Configuration Highlights

### Helm Chart (`extras/helm/hka/`)

The Helm chart configures:
- **Resource limits:** 600m CPU, 1Gi memory
- **Security context:** Non-root user (UID 1002), dropped capabilities, RuntimeDefault seccomp profile
- **Health probes:** Startup probe on `/actuator/health/liveness` with extended retry threshold to accommodate Spring Boot startup time
- **Volume mounts:** TLS certificate, private key, and log directory from host

### Terraform (`extras/terraform/main.tf`)

Uses providers:
- `hashicorp/kubernetes` v3.0.1
- `hashicorp/helm` v3.1.1

Configured for Docker Desktop's Kubernetes context with a 5-minute timeout (configurable via `timeout_app` variable).

---

## API Testing with Bruno

The project includes a Bruno collection with sample requests.

**Environment variables:**
```
apiUrl = https://localhost:8443
```

**Available requests:**
- GET all Hochschulen
- GET by ID
- GET search by name
- POST new Hochschule (valid + invalid examples)
- PUT update Hochschule (valid + invalid examples)

---

## Troubleshooting

### Pod stuck in `ContainerCreating`

Check events:
```powershell
kubectl describe pod -n acme <pod-name>
```

Common causes:
- Missing `medium-priority` PriorityClass → create it (see above)
- Missing log directory on host → create it
- Image not built → run `mvn spring-boot:build-image`

### Pod restarts with `Startup probe failed`

Spring Boot needs ~60-90 seconds to fully start in the container. The `startupProbe.failureThreshold` is configured generously to accommodate slower machines.

### Port 8443 already in use

Stop any running Spring Boot process:
```powershell
Get-Process java | Stop-Process -Force
```

### Terraform timeout

If `terraform apply` times out at 300 seconds, fall back to direct Helm install (commands above). Functionally equivalent — Terraform internally wraps the same Helm release.

---

## Course Context

This project is part of the **Wirtschaftsinformatik** program at **Hochschule Karlsruhe**, course taught by **Prof. Dr. Jürgen Zimmermann**.

The submission covers **Milestones 5-7**:
- **M5:** REST controller, service, and entity implementation
- **M6:** Docker image creation and Kubernetes deployment
- **M7:** POST/PUT endpoints with validation, plus error handling

### Required Tests (per assignment)

The first submission requires three tests:
1. **Read by ID** — `findById()`
2. **Read with search parameter** — `findByName()`
3. **Create new entity** — `post()`

All implemented as `@ParameterizedTest` methods organized in `@Nested` classes within `HochschuleControllerTest.java`.

---

## License

GPL v3 — see `LICENSE` file.

The project template and configurations are © Jürgen Zimmermann, Hochschule Karlsruhe.
