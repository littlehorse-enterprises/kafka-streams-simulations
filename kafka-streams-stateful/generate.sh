#!/bin/bash

set -ex

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd $SCRIPT_DIR

export APP_NAME="generator"
export LOG_LEVEL="INFO"
ARGUMENTS=""

while [ $# -gt 0 ]; do
    case "$1" in
    --build)
        ../gradlew clean installDist -x test
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

CONFIG_PATH="${SCRIPT_DIR}/configs/${APP_NAME}.config"

if [ ! -f "${CONFIG_PATH}" ]; then
    echo "${CONFIG_PATH} does not exist."
    exit 1
fi

./build/install/kafka-streams-stateful/bin/kafka-streams-stateful generator ${ARGUMENTS} ${CONFIG_PATH}
