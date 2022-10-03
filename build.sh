#!/bin/bash

set -e

readonly executable_path="./target/lgdsync"

opts='{}'
if [ -n "$1" ]; then
  opts="{:version \"$1\"}"
fi

jar=$(clojure -T:build uber "$opts")
echo "Created a jar file: $jar"

echo '#!'"$(which java)" '-jar' > "$executable_path"
cat "$jar" >> "$executable_path"
chmod +x "$executable_path"

echo "Created an executable file: $executable_path"
