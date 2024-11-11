#!/bin/bash

set -e
cd $1
watch -n 0.1 -t -c -d "echo 'Partitions with checkpoint file:' && find . -name '.checkpoint' -exec ls -l --time-style=+'%T.%N' {} \; | cut -d ' ' -f '6-8' | sort -V -k1.22"
