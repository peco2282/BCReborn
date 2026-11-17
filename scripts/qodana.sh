#!/usr/bin/env bash
# Run Qodana locally in a throwaway container (image and caches are shared across projects).
# Usage: scripts/qodana.sh [extra qodana args]
# Results: build/qodana/ (qodana.sarif.json, report/)
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
IMAGE="${QODANA_IMAGE:-jetbrains/qodana-jvm-community:latest}"   # match `linter:` in qodana.yaml
CACHE_DIR="${QODANA_CACHE_DIR:-$HOME/.cache/qodana}"             # shared by every project
RESULTS_DIR="$PROJECT_DIR/build/qodana"

mkdir -p "$CACHE_DIR" "$RESULTS_DIR"

docker run --rm \
  --user "$(id -u):$(id -g)" \
  -v "$PROJECT_DIR":/data/project \
  -v "$RESULTS_DIR":/data/results \
  -v "$CACHE_DIR":/data/cache \
  "$IMAGE" \
  --results-dir /data/results --cache-dir /data/cache "$@"
