# simple-cloud-store

[English version](README.md)

![Frontend](https://img.shields.io/badge/frontend-React%2019%20%2B%20Vite%207-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Backend](https://img.shields.io/badge/backend-Java%2021%20microservices-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Infra](https://img.shields.io/badge/infra-Docker%20Compose-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)
![Status](https://img.shields.io/badge/status-sandbox-111827?style=for-the-badge&labelColor=111827&color=5b5ef4)

Full-stack песочница магазина с публичной витриной на React/Vite/TypeScript и тремя Java-микросервисами за Vert.x gateway.

## Сводка

| Область | Детали |
| --- | --- |
| Назначение | Портфолио-демо для сравнения стилей Spring Boot, Quarkus, Vert.x и React в одном store flow. |
| Текущий frontend | `web-react`, React 19.2.1, Vite 7.2.4, TypeScript 5.9.3, PatternFly 6.3.7, Express 5.1.0. |
| Backend services | Catalog API, Inventory API, Gateway API/static edge, PostgreSQL. |
| Compose entrypoint | `docker compose up -d` из корня репозитория. |
| Публичные compose-порты | PostgreSQL `5432`, Gateway `8080`, Web UI `3000`. |

## Быстрый старт

1. Скопируйте шаблон окружения:

```shell
cp .env.example .env
```

2. Заполните placeholders в `.env`. Храните секреты локально и не коммитьте реальные значения.
3. Запустите локальный stack:

```shell
docker compose up -d
```

4. Откройте приложение:

```text
http://localhost:3000
```

Gateway также опубликован на `http://localhost:8080`.

## Скриншоты и архитектура

![Архитектура](assets/coolstore-arch.png)

![Web UI](assets/coolstore-web.png)

## Матрица сервисов

| Сервис | Путь | Стек | Основные routes | Порты | Данные/config | Задокументированные команды |
| --- | --- | --- | --- | --- | --- | --- |
| Web UI | `web-react` | Node 22 container, React 19.2.1, Vite 7.2.4, TypeScript 5.9.3, Express 5.1.0, PatternFly 6.3.7 | Static storefront через Express | Compose `3000`; `PORT` по умолчанию `8080`, если не задан | Вызывает gateway API | `npm ci`; `npm run dev`; `npm run build`; `npm run verify`; `npm run e2e`; `npm run e2e:compose`; `PORT=3000 npm start`; `node --check server.js` |
| Catalog | `catalog-spring-boot` | Spring Boot 3.5.14, Java 21 | `/api/catalog` | Default `8080`; local profile `9000`; внутренний compose port `8080` | PostgreSQL через environment variables | `./gradlew test`; `./gradlew bootJar -x test --no-daemon`; `./gradlew bootRun --args='--spring.profiles.active=local'` |
| Inventory | `inventory-quarkus` | Quarkus 3.33.1, Java 21 | `/api/inventory/{itemId}` | Внутренний compose port `8080` | PostgreSQL через environment variables | `./gradlew test`; `./gradlew build -Dquarkus.package.type=native -x test` |
| Gateway | `gateway-vertx` | Vert.x 5.0.12, Java 21 | `/api/products`; `/health`; static `/*` | Compose `8080`; `HTTP_PORT` default `8080` | Вызывает catalog `/api/catalog` и inventory `/api/inventory/{itemId}` | `./gradlew test`; `./gradlew nativeCompile` |
| Database | Compose service | PostgreSQL | Service data store | Compose `5432` | Настраивается через `.env` | Запускается через `docker compose up -d` |

## Поток запросов

```text
Browser
  -> Web UI на localhost:3000
  -> Gateway на localhost:8080
  -> Catalog service /api/catalog на внутреннем порту 8080
  -> Inventory service /api/inventory/{itemId} на внутреннем порту 8080
  -> PostgreSQL на compose-порту 5432
```

Gateway объединяет вызовы catalog и inventory для `/api/products`. Web service является текущим публичным frontend и построен на React, Vite и TypeScript.

## Команды локальной разработки

### Корневой stack

```shell
docker compose up -d
```

### Web UI

```shell
cd web-react
npm ci
npm run dev
npm run build
npm run verify
npm run e2e
npm run e2e:compose
PORT=3000 npm start
node --check server.js
```

### Catalog service

```shell
cd catalog-spring-boot
./gradlew test
./gradlew bootJar -x test --no-daemon
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Inventory service

```shell
cd inventory-quarkus
./gradlew test
./gradlew build -Dquarkus.package.type=native -x test
```

### Gateway service

```shell
cd gateway-vertx
./gradlew test
./gradlew nativeCompile
```

## Полезные URL

| URL | Назначение |
| --- | --- |
| `http://localhost:3000` | Web storefront в compose stack. |
| `http://localhost:8080` | Публичный gateway edge. |
| `http://localhost:8080/health` | Gateway health endpoint. |
| `http://localhost:8080/api/products` | Агрегированный product API через gateway. |
| `http://localhost:9000/api/catalog` | Catalog API при прямом запуске Spring Boot local profile. |

## Ограничения и безопасность

- Это sandbox и portfolio project, а не production-ready commerce platform.
- Stack не включает production authentication, authorization, payments или user account management.
- `.env.example` является только шаблоном. Храните реальные `.env` values приватно.
- Публичные compose-порты предназначены для локальной разработки: PostgreSQL `5432`, gateway `8080`, web `3000`.
- Catalog и inventory работают за gateway в compose и используют внутренний service port `8080`.
- Границы сервисов намеренно разделены, чтобы репозиторий показывал различия framework styles.

## Статус

Portfolio/demo project. Код опубликован как architecture and implementation reference, а не как production-ready service.
