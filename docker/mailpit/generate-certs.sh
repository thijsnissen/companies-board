#!/usr/bin/env sh

set -euo pipefail

generate_cert() {
  alias=${1:-"mailpit-local"}

  cd "$(dirname "${0}")"
  openssl req -x509 -newkey rsa:4096 -keyout "key.pem" -out "cert.pem" -days 365 -nodes -subj "/CN=localhost"
  keytool -cacerts -delete -alias "${alias}" -storepass changeit -noprompt 2>/dev/null || true
  keytool -cacerts -import -alias "${alias}" -file "cert.pem"
}

generate_cert ${@-}
