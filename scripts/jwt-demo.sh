#!/usr/bin/env bash

export AWS_REGION=us-east-1
export ALB_DNS="$(terraform -chdir=/Users/thurmansanders/AmigosCode/infra/alb output -raw alb_dns_name)"

# Prompt for credentials
read -p "Email: " DEMO_EMAIL
read -s -p "Password: " DEMO_PASSWORD
echo

# Get token
export TOKEN="$(
  curl -s -X POST "http://$ALB_DNS/api/v1/auth/authenticate" \
    -H "Content-Type: application/json" \
    -d "{\"email\":\"$DEMO_EMAIL\",\"password\":\"$DEMO_PASSWORD\"}" \
  | python3 -c 'import sys, json; print(json.load(sys.stdin)["token"])'
)"

# Optional: hide full token in output (cleaner demo)
echo "Token acquired ✅"
echo "$TOKEN"

# Call protected endpoint
curl -i "http://$ALB_DNS/api/v1/products" \
  -H "Authorization: Bearer $TOKEN"