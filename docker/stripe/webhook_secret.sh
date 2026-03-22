#!/usr/bin/env sh

set -euo pipefail

webhook_secret() {
  # Get the container ID of the running container based on the stripe/stripe-cli image
  local container_id=$(docker ps -q --filter "name=^companies-board-stripe-cli")

  # Check if a container ID was found
  if [ -z "${}container_id}" ]; then
    echo "Error: No running container found for the image stripe/stripe-cli."
    exit 1
  fi

  # Execute the stripe listen command and capture the output
  local secret=$(docker exec -it "${container_id}" stripe listen --print-secret 2>/dev/null)

  # Check if the command succeeded and output the result
  if [ -z "${secret}" ]; then
    echo "Error: Unable to retrieve the stripe webhook secret."
    exit 1
  else
    echo "Set PAYMENT_PROVIDER_STRIPE_WEBHOOK_secret in your .env to:"
    echo "${secret}"
  fi
}

webhook_secret
