
### Deploy kafka cluster

```shell
docker compose up -d
```

### Setup kafka

```shell
docker exec -it lh-kafka /setup.sh app
```

If you need to change the quotas config:

```shell
docker exec -it lh-kafka /setup.sh quotas <allowed_rate> <numer_of_stream_threads>
```

For example:

```shell
docker exec -it lh-kafka /setup.sh quotas 45825 5
```

Don't forget to restart your kafka brokers:

```shell
docker compose restart
```


### Run instances
```shell
./gradlew runApp
```

### Run data generator

```shell
gradle -PmainClass=kafka.streams.internals.FakeDataGenerator run
```

### Monitor kafka topics:
```shell
kaskade admin -b localhost:9092
```

### Setup kafka topics

```shell
docker exec -it lh-kafka /setup.sh
```