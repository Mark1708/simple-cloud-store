FROM ubuntu:latest
WORKDIR /app
COPY build/native/nativeCompile/gateway .
EXPOSE 8080
ENTRYPOINT ["/app/gateway", "run", "cloud.store.gateway.GatewayVerticle"]
