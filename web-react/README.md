# web-react

Web UI for the Simple Cloud Store demo. The runtime frontend is React, Vite, and TypeScript.

## Run locally

```shell
npm ci
npm run build
COOLSTORE_GW_ENDPOINT=http://localhost:8080 PORT=3000 npm start
```

For Vite development mode:

```shell
npm run dev
```

## Runtime configuration

The Node host exposes `/config.js`, which sets `window.COOLSTORE_CONFIG` for the browser app.

- `COOLSTORE_GW_ENDPOINT` configures the HTTP gateway endpoint.
- `SECURE_COOLSTORE_GW_ENDPOINT` configures the HTTPS gateway endpoint.
- `PORT` configures the Node host port.

## Maintenance notes

These commands are useful when changing the web UI. They are not required for simply running the main demo path.

```shell
npm run typecheck
npm run lint
npm run test
npm run build
npm run security-check
npm run verify
node --check server.js
```
