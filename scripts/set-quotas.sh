#!/bin/bash

set -e

if [[ $# -lt 2 ]]; then
    echo "USAGE: ./set-quotas.sh <allowed rate in bytes> <client id>"
    exit 1
fi

allowed_rate=$1
client_id=$2

bootstrap_servers=${BOOTSTRAP_SERVERS:-127.0.0.1:19092}

/opt/kafka/bin/kafka-configs.sh --alter \
    --bootstrap-server $bootstrap_servers \
    --add-config 'producer_byte_rate='$allowed_rate',consumer_byte_rate='$allowed_rate'' \
    --entity-type clients --entity-name $client_id
