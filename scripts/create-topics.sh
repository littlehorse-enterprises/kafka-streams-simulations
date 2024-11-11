#!/bin/bash

set -e

if [[ $# -lt 3 ]]; then
    echo "USAGE: ./create-topics.sh <partitions> <replication factor> <topic 1> <topic 2> ..."
fi

partitions=$1
replication_factor=$2

shift 2
topics=$@

bootstrap_servers=${BOOTSTRAP_SERVERS:-127.0.0.1:19092}

for topic_name in $topics; do
    /opt/kafka/bin/kafka-topics.sh --create \
        --bootstrap-server $bootstrap_servers \
        --replication-factor $replication_factor \
        --partitions $partitions \
        --topic $topic_name
done
