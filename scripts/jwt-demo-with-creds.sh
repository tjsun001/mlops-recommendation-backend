export AWS_REGION=us-east-1
export ALB_DNS="$(terraform -chdir=/Users/thurmansanders/AmigosCode/infra/alb output -raw alb_dns_name)"

export TOKEN="$(
  curl -s -X POST "http://$ALB_DNS/api/v1/auth/authenticate" \
    -H "Content-Type: application/json" \
    -d '{"email":"demo@example.com","password":"demo123"}' \
  | python3 -c 'import sys, json; print(json.load(sys.stdin)["token"])'
)"

echo "$TOKEN"

curl -i "http://$ALB_DNS/api/v1/products" \
  -H "Authorization: Bearer $TOKEN"