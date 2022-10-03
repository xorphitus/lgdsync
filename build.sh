#!/bin/bash

readonly executable_path="./target/lgdsync"

clj -T:build clean
clj -T:build uber
echo "#!/usr/bin/java -jar" > "$executable_path"
cat ./target/lgdsync-*-standalone.jar >> "$executable_path"
chmod +x "$executable_path"

echo "Created an executable file: $executable_path"
