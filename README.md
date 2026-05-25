# simple-cloud-store

[Русская версия](README.ru.md)

## Overview

Full-stack microservices demo exploring different Java frameworks (Spring Boot, Quarkus, Vert.x) in a single application. Sandbox project for comparing framework styles and learning cloud infrastructure patterns.

## Architecture

![Architecture](assets/coolstore-arch.png)

## Services

- **Catalog Service** (Spring Boot) -- REST API for product catalog, backed by PostgreSQL
- **Inventory Service** (Quarkus) -- REST API for inventory management, backed by PostgreSQL
- **Gateway Service** (Vert.x) -- API gateway routing requests to catalog and inventory
- **Web UI** (Node.js + React/Vite) -- storefront calling the gateway

## Tech stack

- Java 21, GraalVM
- Spring Boot 3.5.14
- Quarkus 3.33.1 LTS
- Vert.x 5.0.12
- Node.js 22 LTS
- React 19 + Vite 7 + TypeScript
- Docker

## Quick start

1. Copy the environment template:

```shell
cp .env.example .env
```

2. Fill in the placeholder values in `.env`.
3. Start the services:

```shell
docker compose up -d
```

4. Open the web UI at `http://localhost:3000`.

## Hygiene

- Keep config examples secret-free; replace all placeholder values before use.
- Keep service-specific runtime settings in environment variables or local profiles.
- This is a demo setup with no production authentication, payments, or user management.

## Verification notes

- Java service tests are expected to run with local H2-backed test settings.
- The Web UI uses a modern verification gate: `npm run verify` plus `node --check server.js`.
- `npm run security-check` uses `npm audit --audit-level=high`. Current npm audit output may still report moderate transitive findings that do not fail the high-severity gate.

## Limitations

- The frontend lives in `web-react` and runs on React/Vite/TypeScript.
- Spring Boot, Quarkus, Vert.x, and Node.js services remain separate on purpose so the repository can compare framework styles.
- This project is not production-ready and should not be used as a template for production authentication, payments, or user accounts.

## Status

- Sandbox / learning project.
