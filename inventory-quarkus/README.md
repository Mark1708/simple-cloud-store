# inventory-quarkus

Сервис описи запасов (Inventory Service) - использует RESТ АРІ для доступа к описи запасов товаров, хранящейся в реляционной базе данных

## Запуск приложения
```
# Сборка нативного образа
./gradlew build -Dquarkus.package.type=native  -x test
./gradlew build -Dquarkus.package.type=native -Dquarkus.profile=local  -x test
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true

# Запуск приложения
./build/inventory-quarkus-1.0.0-SNAPSHOT-runner

# Сборка 
docker build -f ./Dockerfile -t inventory .
```

## Полезные ссылки
- [Starter](https://code.quarkus.io/)