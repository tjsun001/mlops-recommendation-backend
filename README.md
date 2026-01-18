
This project demonstrates a production-style MLOps architecture focused on reliability, graceful degradation, and operational correctness — rather than offline model experimentation.

The system integrates a frontend application, a backend API layer, and an ML inference service.  
A core design goal is ensuring the system always returns a valid, explainable response, even when machine-learning predictions are unavailable, unreliable, or failing.

---

## System Architecture

### Components

**Frontend (Next.js)**  
User interface for requesting and displaying product recommendations.

**Backend API (Spring Boot)**  
Acts as the system boundary. Owns API contracts, validation, orchestration, and fallback logic.

**Inference Service (FastAPI)**  
Isolated ML service responsible for loading trained model artifacts and returning predictions.

**Database (PostgreSQL)**  
Stores product data and (optionally) user or event data.

---

## Key Design Principle

**The backend acts as the reliability boundary.**

Machine learning is used when available, but is never allowed to violate system guarantees.  
If inference is slow, unavailable, or incorrect, the backend degrades gracefully to deterministic logic.

---

## Key Behaviors

**Cold-start handling**  
If ML returns empty or insufficient results, the backend falls back to deterministic “popular items”.

**Graceful degradation**  
If inference errors, times out, or becomes unreachable, the system still returns a valid response.

**Explainability**  
Responses include metadata (`source`, `reason`) indicating whether ML or fallback logic was used.

**Separation of concerns**  
UI, backend, and inference services are independently deployable and independently scalable.

---

## Environment Configuration (Local vs Remote Inference)

The backend is environment-agnostic and selects its inference target entirely via configuration.

**No code changes are required** to switch between local and remote inference.

| Environment | INFERENCE_BASE_URL |
|-----------|--------------------|
| Local (Docker Compose) | `http://inference:8000` |
| Remote / Production | `http://<host>:8000` |

If `INFERENCE_BASE_URL` is not explicitly set, the backend defaults to local Docker inference.

---

## Repositories

**Frontend (Next.js)**  
UI for requesting and displaying recommendations  
👉 link to frontend repo

**Backend API (Spring Boot)**  
Owns API contracts, integrates ML inference, and enforces fallback logic  
👉 link to backend repo

**Inference Service (FastAPI)**  
Loads trained model artifacts and serves predictions via REST  
👉 link to inference repo

---

## Technology Stack

- Java, Spring Boot
- Python, FastAPI
- Next.js
- PostgreSQL
- Docker
- AWS (deployment & monitoring)
- REST APIs

---

## Why This Project Exists

This project was built to explore how ML systems behave in real production environments, including:

- sparse or missing data
- cold-start users
- model uncertainty
- downstream service failures

The focus is operational ML, not offline model accuracy.

---

## Kill-Switch Demo (Inference Resilience)

This service integrates with an external ML inference service for product recommendations.  
To demonstrate graceful degradation and service isolation, you can simulate an inference outage.

### Normal operation (ML enabled)
```bash
curl http://localhost:5050/recommendations/1
