# simple-cloud-store

[English version](README.md)

## Обзор

Демонстрационное полнофункциональное микросервисное приложение, использующее разные Java-фреймворки (Spring Boot, Quarkus, Vert.x) в рамках одного проекта. Песочница для сравнения стилей разных фреймворков и изучения паттернов облачной инфраструктуры.

## Архитектура

![Архитектура](assets/coolstore-arch.png)

## Сервисы

- **Catalog Service** (Spring Boot) -- REST API для каталога товаров, данные в PostgreSQL
- **Inventory Service** (Quarkus) -- REST API для управления запасами, данные в PostgreSQL
- **Gateway Service** (Vert.x) -- API-шлюз, маршрутизирующий запросы к сервисам каталога и запасов
- **Web UI** (Node.js + AngularJS) -- витрина магазина, обращающаяся к шлюзу

## Стек технологий

- Java 21, GraalVM
- Spring Boot 3.5.14
- Quarkus 3.33.1 LTS
- Vert.x 4.5.22
- Node.js 22 LTS
- AngularJS 1.8 legacy/frozen
- Docker

## Быстрый старт

1. Скопируйте шаблон окружения:

```shell
cp .env.example .env
```

2. Заполните значения-заглушки в файле `.env`.
3. Запустите сервисы:

```shell
docker compose up -d
```

4. Откройте веб-интерфейс: `http://localhost:3000`.

## Гигиена

- Не используйте учебные учётные данные вне локальной разработки.
- Храните runtime-настройки сервисов в переменных окружения или local profiles.
- Это демонстрационная настройка без продакшен-аутентификации, платежей и управления пользователями.

## Заметки о проверке

- Тесты Java-сервисов рассчитаны на локальные test settings с H2.
- Для legacy Web UI текущая поддерживаемая синтаксическая проверка: `node --check server.js`.
- Исторические скрипты `npm run lint` и `npm run security-check` сохранены для traceability, но считаются известными legacy scripts: стек XO несовместим с текущим Node.js runtime, а `nsp` discontinued.

## Ограничения

- AngularJS 1.8 намеренно оставлен как legacy frontend layer. Он достиг end-of-life и заморожен в этом репозитории.
- Frontend не переписывается на React, Vue или современный Angular в рамках этого demo.
- Spring Boot, Quarkus, Vert.x и Node.js сервисы намеренно остаются отдельными, чтобы репозиторий сохранял ценность сравнения разных frameworks.
- Проект не production-ready и не должен использоваться как шаблон для production authentication, payments или user accounts.

## Статус

- Песочница / учебный проект.
