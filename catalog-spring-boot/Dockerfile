FROM ubuntu:latest
WORKDIR /app
COPY build/native/nativeCompile/catalog .
EXPOSE 8080
ENTRYPOINT ["/app/catalog"]