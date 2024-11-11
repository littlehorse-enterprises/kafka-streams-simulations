#!/bin/bash

set -e

tail -f $1.log | grep "dirty"