# web-nodejs-angular

Сервис веб-интерфейса (WebUl Service) - вызывает сервис шлюза, чтобы получить необходимую информацию

## Запуск приложения
```shell
# Установка зависимостей
npm install

# Запуск
COOLSTORE_GW_ENDPOINT=http://localhost:8090 PORT=3000 npm start
```

## Legacy scripts

This service intentionally keeps the historical AngularJS/Node.js toolchain for demo traceability.
Use `node --check server.js` as the supported syntax check on the current Node.js runtime.

Known legacy scripts:

- `npm run lint` uses the old XO stack and is not compatible with the current Node.js runtime.
- `npm run security-check` calls discontinued `nsp` tooling.
