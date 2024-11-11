#!/bin/bash

set -e

if [[ $# -lt 1 ]]; then
    echo "USAGE: $0 <state dir>"
    exit 1
fi

watch -n 0.1 -t -c -d "echo 'Partitions with checkpoint file:' && find $1 -name '.checkpoint' -exec ls -l --time-style=+'%T.%N' {} \; | cut -d ' ' -f '6-7' | sort -V -k1.20"
