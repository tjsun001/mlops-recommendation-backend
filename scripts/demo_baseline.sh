#!/usr/bin/env bash
set -euo pipefail
BASE_URL="${1:-http://44.206.28.191:5050}"

echo "== Demo baseline: $BASE_URL =="

echo "[health]"
curl -fsS "$BASE_URL/api/inference/health" && echo -e "\n"

echo "[products]"
curl -fsS "$BASE_URL/api/v1/products" | head -c 500 && echo -e "\n\n..."

echo "[recs user 1]"
curl -fsS "$BASE_URL/recommendations/1" && echo -e "\n"

echo "[recs user 2]"
curl -fsS "$BASE_URL/recommendations/2" && echo -e "\n"

echo "[cold start user]"
curl -fsS "$BASE_URL/recommendations/999999" && echo -e "\n"

echo "✅ Baseline PASS"
