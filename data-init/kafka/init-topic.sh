#!/bin/bash

echo -e "Initializing default kafka topics"

kafka-topics.sh --bootstrap-server kafka:9092 --create --if-not-exists --topic VEHICLE_DIRECTION_UPDATED --replication-factor 1 --partitions 1
kafka-topics.sh --bootstrap-server kafka:9092 --list

echo -e "Done initializing kafka topics"