#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd $SCRIPT_DIR

export APP_NAME="app1"

if [ -n "$1" ]; then
    export APP_NAME="$1"
fi

CONFIG_PATH="${SCRIPT_DIR}/dev/${APP_NAME}.config"

if [ ! -f "$CONFIG_PATH" ]; then
    echo "$CONFIG_PATH does not exist."
    exit 1
fi

# TODO: flag --install

./gradlew clean installDist -x test -PmainClass=io.littlehorse.simulations.stateful.app.App

./app/build/install/app/bin/app "$CONFIG_PATH"
