#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
WORK_DIR=$(cd "$SCRIPT_DIR/.." && pwd)

export APP_NAME="generate"

if [ -n "$1" ]; then
  export APP_NAME="$1"
fi

CONFIG_PATH="${SCRIPT_DIR}/dev/${APP_NAME}.config"

if [ ! -f "$CONFIG_PATH" ]; then
  echo "$CONFIG_PATH does not exist."
  exit 1
fi

# TODO: add argument ptocessor for --fail parameter

./gradlew clean installDist -x test -PmainClass=io.littlehorse.simulations.stateful.generator.FakeDataGenerator

app/build/install/app/bin/app "$CONFIG_PATH"
