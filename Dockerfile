# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:e1eeec12952b877762d2a0a94a216600b34056942f6d82635a1d4a5dc8ee9770

WORKDIR /app

# Copy Quarkus JVM build output
COPY build/quarkus-app/lib/ /app/lib/
COPY build/quarkus-app/*.jar /app/
COPY build/quarkus-app/app/ /app/app/
COPY build/quarkus-app/quarkus/ /app/quarkus/

# Distroless uses a fixed non-root user already
EXPOSE 8080

# Quarkus JVM entrypoint
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
