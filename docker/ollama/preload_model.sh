#!/usr/bin/env sh

set -euo pipefail

preload_model() {
  local host="${1}"
  local port="${2}"
  local model="${3}"

  until curl -v "http://${host}:${port}"; do
    sleep 1
  done

  curl -v http://${host}:${port}/api/pull -d '{"model": "'${model}'", "stream": false}'
  curl -v http://${host}:${port}/api/generate -d '{"model": "'${model}'"}'
}

preload_model "${@}"
