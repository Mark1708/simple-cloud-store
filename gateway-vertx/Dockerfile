FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts ./
COPY src src
RUN ./gradlew shadowJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-fat.jar gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/gateway.jar", "run", "cloud.store.gateway.GatewayVerticle"]
