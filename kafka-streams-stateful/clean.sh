#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)
cd $SCRIPT_DIR

./gradlew clean
rm -f *.log
rm -rf /tmp/instance-data*