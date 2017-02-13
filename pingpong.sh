#!/usr/bin/env sh
echo "pingpong args: $@"
exec java -jar /root/basic-project-0.1.0.jar "$@"