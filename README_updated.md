# MLOps Recommendation System

## Overview
End-to-end MLOps system demonstrating:
- JWT-secured backend (Spring Boot)
- ML inference service (FastAPI)
- AWS deployment (ECS + ALB)
- Observability (CloudWatch)

## Flow
1. User authenticates → JWT issued  
2. Frontend calls backend with token  
3. Backend calls inference service  
4. Fallback used if ML unavailable  

## Tech Stack
- Spring Boot, FastAPI
- AWS ECS, ALB, ECR
- Kafka (Outbox pattern)
- CloudWatch

## Key Feature
Backend acts as a **reliability boundary** ensuring system stability even if ML fails.
