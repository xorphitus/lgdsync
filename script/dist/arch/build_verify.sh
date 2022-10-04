#!/usr/bin/env bash
if [ $# -ne 2 ]; then
  echo "Usage: $0 <version> <sha256 hash of the jar file>"
  exit 1
fi

cd $(dirname "${BASH_SOURCE[0]}")

# Trim the 'v' prefix - Arch packages don't have it
version=$(echo "$1" | sed -e 's/^v//')
sha256=$2
sed -e "s/{{ version }}/$version/g" PKGBUILD.tpl | sed -e "s/{{ sha256 }}/$sha256/g" > PKGBUILD
docker build . -t lgdsync-arch-test
