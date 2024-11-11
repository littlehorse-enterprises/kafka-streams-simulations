### Deploy kafka cluster

```shell
docker compose up -d
```

### Update quotas

If you need to change the quotas config:

```shell
docker compose exec -it cli /scripts/set-quotas.sh <allowed rate in bytes> <client id>
```

For example:

```shell
for n in {1..5}; do \
    docker compose exec -it cli /scripts/set-quotas.sh 45000 app1-StreamThread-$n-restore-consumer; done
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
