#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
WORK_DIR=$(cd "$SCRIPT_DIR/.." && pwd)

CONFIG_NAME="app1"

if [ -n "$1" ]; then
  CONFIG_NAME="$1"
fi

CONFIG_PATH="${SCRIPT_DIR}/dev/${CONFIG_NAME}.config"

if [ ! -f "$CONFIG_PATH" ]; then
  echo "$CONFIG_PATH does not exist."
  exit 1
fi

./gradlew clean installDist -x test -PmainClass=kafka.streams.internals.FakeDataGenerator

app/build/install/app/bin/app "$CONFIG_PATH"