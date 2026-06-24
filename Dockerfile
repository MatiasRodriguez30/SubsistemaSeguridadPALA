FROM gradle:8.8-jdk17-alpine AS builder

WORKDIR /workspace

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean bootJar -x test

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=builder /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
