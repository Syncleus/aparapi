#!/usr/bin/env bash
# Validates RUN_TESTS / script section for Travis (issue #95).
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

echo "==> YAML parse"
ruby -e 'require "yaml"; YAML.load_file(".travis.yml"); puts "yaml ok"'

echo "==> global RUN_TESTS default"
grep -q 'RUN_TESTS=true' .travis.yml

echo "==> matrix uses RUN_TESTS (not RUN_TEST typo)"
if grep -q 'RUN_TEST=false' .travis.yml; then
  echo "FAIL: found RUN_TEST=false typo; use RUN_TESTS=false"
  exit 1
fi
grep -q 'RUN_TESTS=false' .travis.yml

echo "==> script section gates mvn test"
grep -q '^script:' .travis.yml
grep -q 'RUN_TESTS' .travis.yml
grep -q 'mvn test' .travis.yml
grep -q 'skipTests' .travis.yml

echo "All checks passed."
