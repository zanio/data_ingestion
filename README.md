# Data Ingestion Platform

Please use this readme as your projects readme. You can find instructions for
the challenge in the [`INSTRUCTIONS.pdf`](INSTRUCTIONS.pdf) file.

## Managing kafka instance on docker
### Create Kafka topic
docker exec kafka kafka-topics --create --bootstrap-server localhost:29092 --partitions 1 --replication-factor 1 --topic VEHICLE_DIRECTION_UPDATED
### View messages from a particular topic
docker exec kafka kafka-console-consumer --topic VEHICLE_DIRECTION_UPDATED --from-beginning --max-messages 10 --bootstrap-server localhost:29092
### List all topic
docker exec kafka kafka-topics --list --bootstrap-server localhost:29092
