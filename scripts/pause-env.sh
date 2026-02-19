#!/usr/bin/env bash
set -euo pipefail

AWS_REGION="${AWS_REGION:-us-east-1}"
CLUSTER="${CLUSTER:-mlops-poc-dev-ecs-cluster}"

# Change these if your service names differ
SERVICES=(
  "mlops-poc-dev-product-service"
  "mlops-poc-dev-inference-service"
  "mlops-poc-dev-postgres-service"
)

echo "Pausing environment..."
echo "Region : $AWS_REGION"
echo "Cluster: $CLUSTER"
echo

echo "Current desired/running counts:"
aws ecs describe-services \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --services "${SERVICES[@]}" \
  --query "services[].{Service:serviceName,Desired:desiredCount,Running:runningCount,Pending:pendingCount,TaskDef:taskDefinition}" \
  --output table || true

echo
for SVC in "${SERVICES[@]}"; do
  echo "Scaling down: $SVC -> desired=0"
  aws ecs update-service \
    --region "$AWS_REGION" \
    --cluster "$CLUSTER" \
    --service "$SVC" \
    --desired-count 0 >/dev/null
done

echo
echo "Waiting for services to reach Running=0..."
for SVC in "${SERVICES[@]}"; do
  aws ecs wait services-stable \
    --region "$AWS_REGION" \
    --cluster "$CLUSTER" \
    --services "$SVC" || true
done

echo
echo "Post-pause desired/running counts:"
aws ecs describe-services \
  --region "$AWS_REGION" \
  --cluster "$CLUSTER" \
  --services "${SERVICES[@]}" \
  --query "services[].{Service:serviceName,Desired:desiredCount,Running:runningCount,Pending:pendingCount}" \
  --output table || true

echo
echo "✅ Pause complete."
