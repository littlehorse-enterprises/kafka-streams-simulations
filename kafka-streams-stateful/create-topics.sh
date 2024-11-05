#!/bin/bash

# Function to create a Kafka topic
create_topic() {
    local topic_name=$1
    local partitions=$2
    local replication_factor=1

    /opt/kafka/bin/kafka-topics.sh --create \
        --bootstrap-server localhost:9092 \
        --replication-factor $replication_factor \
        --partitions $partitions \
        --topic $topic_name

    if [ $? -eq 0 ]; then
        echo "Topic '$topic_name' created successfully."
    else
        echo "Failed to create topic '$topic_name'."
    fi
}

set_quotas() {
  local client_id=$1
  local allowed_rate=$2
  /opt/kafka/bin/kafka-configs.sh --bootstrap-server localhost:9092 \
    --alter --add-config 'producer_byte_rate='$allowed_rate',consumer_byte_rate='$allowed_rate'' \
    --entity-type clients --entity-name $client_id
}

configure_quotas() {
  local allowed_rate=$1
  local num_threads=$2
  for ((i=1; i<=num_threads; i++))
  do
      set_quotas "my-app-StreamThread-$i-producer" "$allowed_rate"
  done
  set_quotas "my-app" 10085760
}

configure_app() {
  num_threads=$1
  allowed_rate=$2
  # Create the topic
  create_topic "input-topic" 24
  create_topic "output-topic" 24
  configure_quotas $allowed_rate $num_threads
}

if [ "$1" == "quotas" ]; then
  num_threads=$3
  allowed_rate=$2
  configure_quotas "$allowed_rate" "$num_threads"
fi

if [ "$1" == "app" ]; then
  num_threads=${STREAM_THREAD_COUNT:-1}
  configure_app "$num_threads" 10085760
fi
