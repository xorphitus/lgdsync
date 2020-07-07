#!/bin/bash

readonly executable_path="./target/uberjar/lgdsync"

lein clean
lein uberjar
echo "#!/usr/bin/java -jar" > "$executable_path"
cat ./target/uberjar/lgdsync-*-standalone.jar >> "$executable_path"
chmod +x "$executable_path"

echo "Created an executable file: $executable_path"
