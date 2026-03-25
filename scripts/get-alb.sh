# scripts/get-alb.sh
export ALB_DNS=$(aws elbv2 describe-load-balancers \
  --region us-east-1 \
  --names mlops-poc-dev-alb \
  --query 'LoadBalancers[0].DNSName' \
  --output text)

echo "ALB_DNS=$ALB_DNS"