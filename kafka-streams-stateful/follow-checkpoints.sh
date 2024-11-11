#!/bin/bash

set -e
cd $1
watch -n 1 -t -c -d "echo 'Partitions with checkpoint file:' && find . -name '.checkpoint' -printf '%h\n' | sort -V"