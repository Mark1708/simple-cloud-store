# Gateway-vertx

Сервис шлюза (Gateway Service) - принимает запросы и передает их сервису каталогов или сервису описи запасов

## Запуск приложения
```
# Сборка нативного образа
./gradlew nativeCompile

# Запуск нативного приложения
./build/native/nativeCompile/gateway

# Сборка
docker build -f ./Dockerfile -t gateway .
```

## Полезные ссылки
- [Starter](https://start.vertx.io/)
