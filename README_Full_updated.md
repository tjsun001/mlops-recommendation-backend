# Full MLOps Recommendation System Documentation

## Architecture
- Frontend → Backend → Inference Service
- Backend orchestrates and secures all requests
- ML service is decoupled and optional

## Security
- JWT-based authentication
- Email as identity (not userId)
- Secret injected via environment variables

## Deployment
- GitHub Actions → ECR → ECS
- ALB routes traffic

## Observability
- CloudWatch metrics:
  - RequestCount
  - Latency
  - 4XX errors

## Reliability
Fallback logic ensures:
- No ML dependency failure
- Consistent API response

## Demo Flow
- Login → Token → API call → Response
