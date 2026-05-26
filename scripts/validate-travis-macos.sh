#!/usr/bin/env bash
# Validate the macOS Travis matrix fix for issue #35.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

echo "==> YAML parse"
ruby -e 'require "yaml"; YAML.load_file(".travis.yml"); puts "yaml ok"'

echo "==> macOS matrix entry enabled"
grep -q '^[[:space:]]*- os: osx' .travis.yml || {
  echo "FAIL: expected active '- os: osx' matrix entry"
  exit 1
}

echo "==> macOS before_install skips Homebrew OpenCV"
if awk '/TRAVIS_OS_NAME.*osx/,/fi/ {print}' .travis.yml | grep -q 'brew install.*opencv'; then
  echo "FAIL: macOS block still runs 'brew install ... opencv'"
  exit 1
fi

echo "==> whitespace"
git diff --check

echo "All checks passed."
