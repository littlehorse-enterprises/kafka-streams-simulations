#!/bin/bash

set -ex

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd $SCRIPT_DIR

export APP_NAME="app1"

if [ -n "$1" ]; then
    export APP_NAME="$1"
else
  echo ""
fi

CONFIG_PATH="${SCRIPT_DIR}/dev/${APP_NAME}.config"

if [ ! -f "$CONFIG_PATH" ]; then
    echo "$CONFIG_PATH does not exist."
    exit 1
fi

export LOG_LEVEL="INFO"
ARGUMENTS=""
while [ $# -gt 0 ]; do
    case "$1" in
    --build)
        ./gradlew clean installDist -x test
        ;;
    --debug)
        export LOG_LEVEL="DEBUG"
        ;;
    *)
        ARGUMENTS="${ARGUMENTS} $1"
        ;;
    esac
    shift
done

./app/build/install/app/bin/app $ARGUMENTS
