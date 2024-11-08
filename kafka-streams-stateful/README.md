### Deploy kafka cluster

```shell
docker compose up -d
```

### Setup kafka

```shell
docker compose exec -it cli /setup.sh app
```

If you need to change the quotas config:

```shell
docker compose exec -it cli /setup.sh quotas <allowed_rate> <numer_of_stream_threads>
```

For example:

```shell
docker compose exec -it cli /setup.sh quotas 45825 5
```

Don't forget to restart your kafka brokers:

```shell
docker compose restart
```

### Run instances

```shell
./run.sh app1
```

### Run data generator

```shell
./generate.sh
```

### Monitor kafka topics:

```shell
kaskade admin -b localhost:19092
```
