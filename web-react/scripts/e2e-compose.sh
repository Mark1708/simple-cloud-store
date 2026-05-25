#!/usr/bin/env sh
set -eu

compose() {
  docker compose --project-directory .. "$@"
}

cleanup() {
  compose down -v
}

wait_for_url() {
  url="$1"
  label="$2"
  attempts=90

  while [ "$attempts" -gt 0 ]; do
    if curl --fail --silent --show-error "$url" >/dev/null 2>&1; then
      return 0
    fi

    attempts=$((attempts - 1))
    sleep 2
  done

  printf '%s did not become ready at %s\n' "$label" "$url" >&2
  compose ps >&2
  compose logs db catalog inventory gateway web >&2
  return 1
}

trap cleanup EXIT INT TERM

compose up -d --build db catalog inventory gateway web
wait_for_url http://localhost:8080/health gateway
wait_for_url http://localhost:3000 web

npm run e2e
