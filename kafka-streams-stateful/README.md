### Deploy kafka cluster:

```shell
docker compose up -d
```

### Update quotas:

If you need to change the quotas config:

```shell
docker compose exec -it cli /scripts/set-quotas.sh <allowed rate in bytes> <client id>
```

For example:

```shell
for n in {1..5}; do \
    docker compose exec -it cli /scripts/set-quotas.sh 45000 app1-StreamThread-$n-restore-consumer; done
```

### Run instances:

```shell
./run.sh app1
```

> Other options \
> `--build`: build before run \
> `--debug`: debug log level \
> `--dsl`: use dsl

### Run data generator:

```shell
./generate.sh
```

> Other options \
> `--build`: build before run \
> `--debug`: debug log level \
> `--limit <number>`: total messages to send \
> `--fail`: send a fail message

### Clean store:

```shell
./clean.sh
```

### List checkpoint files in real time:

```shell
./follow-checkpoints.sh /tmp/instance-data-1
```

### Monitor kafka topics:

```shell
kaskade admin -b localhost:19092
```
