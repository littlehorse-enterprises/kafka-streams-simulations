#!/bin/bash

set -ex

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd $SCRIPT_DIR

export APP_NAME="app1"
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
        export APP_NAME="$1"
        ;;
    esac
    shift
done

CONFIG_PATH="${SCRIPT_DIR}/configs/${APP_NAME}.config"

if [ ! -f "${CONFIG_PATH}" ]; then
    echo "${CONFIG_PATH} does not exist."
    exit 1
fi

./app/build/install/app/bin/app streams ${CONFIG_PATH}
