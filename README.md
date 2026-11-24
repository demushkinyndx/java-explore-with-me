# ExploreWithMe


```
explore-with-me/
├── stats/
│   ├── stats-dto      # общие DTO
│   ├── stats-server   # HTTP-сервис статистики (порт 9090)
│   └── stats-client   # HTTP-клиент
└── ewm-service        # основной сервис (заглушка на этапе 1)
```


## Запуск


```bash
docker compose up --build -d stats-db stats-server
```


## API
Базовый URL: `http://localhost:9090`

### Сохранить hit

```bash
curl -i -X POST http://localhost:9090/hit \
  -H 'Content-Type: application/json' \
  -d '{
    "app": "ewm-main-service",
    "uri": "/events/1",
    "ip": "192.163.0.1",
    "timestamp": "2022-09-06 11:00:23"
  }'
```

Ожидается: `201 Created`.

### Получить статистику

```bash
curl -i -G http://localhost:9090/stats \
  --data-urlencode 'start=2022-09-06 00:00:00' \
  --data-urlencode 'end=2022-09-07 00:00:00' \
  --data-urlencode 'uris=/events/1' \
  --data-urlencode 'unique=false'
```

Ожидается: `200 OK` и JSON-массив вида:

```json
[{"app":"ewm-main-service","uri":"/events/1","hits":1}]
```

Параметр `unique=true` считает только уникальные IP.

### Postman
postman/feature.json