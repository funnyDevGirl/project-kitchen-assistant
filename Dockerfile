FROM gradle:8.10.2-jdk21 AS builder

WORKDIR /app

COPY . /app

RUN gradle clean build -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/build/libs/kitchen-assistant-0.0.1-SNAPSHOT-plain.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
