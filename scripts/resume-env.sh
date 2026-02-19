#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-us-east-1}"
CLUSTER="${CLUSTER:-mlops-poc-dev-ecs-cluster}"

# Desired counts (override by exporting env vars before running)
POSTGRES_DESIRED="${POSTGRES_DESIRED:-1}"
INFERENCE_DESIRED="${INFERENCE_DESIRED:-1}"
PRODUCT_DESIRED="${PRODUCT_DESIRED:-1}"

POSTGRES_SERVICE="${POSTGRES_SERVICE:-mlops-poc-dev-postgres-service}"
INFERENCE_SERVICE="${INFERENCE_SERVICE:-mlops-poc-dev-inference-service}"
PRODUCT_SERVICE="${PRODUCT_SERVICE:-mlops-poc-dev-product-service}"

echo "Resuming environment..."
echo "Region : $AWS_REGION"
echo "Cluster: $CLUSTER"
echo

echo "Scaling up Postgres: $POSTGRES_SERVICE -> desired=$POSTGRES_DESIRED"
aws ecs update-service \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --service "$POSTGRES_SERVICE" \
  --desired-count "$POSTGRES_DESIRED" >/dev/null

aws ecs wait services-stable \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --services "$POSTGRES_SERVICE" || true

echo
echo "Scaling up Inference: $INFERENCE_SERVICE -> desired=$INFERENCE_DESIRED"
aws ecs update-service \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --service "$INFERENCE_SERVICE" \
  --desired-count "$INFERENCE_DESIRED" >/dev/null

aws ecs wait services-stable \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --services "$INFERENCE_SERVICE" || true

echo
echo "Scaling up Product: $PRODUCT_SERVICE -> desired=$PRODUCT_DESIRED"
aws ecs update-service \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --service "$PRODUCT_SERVICE" \
  --desired-count "$PRODUCT_DESIRED" >/dev/null

aws ecs wait services-stable \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --services "$PRODUCT_SERVICE" || true

echo
echo "Final desired/running counts:"
aws ecs describe-services \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --services "$POSTGRES_SERVICE" "$INFERENCE_SERVICE" "$PRODUCT_SERVICE" \
  --query "services[].{Service:serviceName,Desired:desiredCount,Running:runningCount,Pending:pendingCount,TaskDef:taskDefinition}" \
  --output table || true

echo
echo "✅ Resume complete."
