#!/bin/bash

echo "Init kafka topics";

docker compose exec -T kafka kafka-topics.sh \
    --create \
    --topic devices \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1;

docker compose exec -T kafka kafka-topics.sh \
    --create \
    --topic modules \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1;

docker compose exec -T kafka kafka-topics.sh \
    --create \
    --topic telemetries \
    --bootstrap-server localhost:9092 \
    --partitions 3 \
    --replication-factor 1;


docker compose exec -T kafka kafka-topics.sh --list --bootstrap-server localhost:9092;

echo "Kafka topics have been inited";
