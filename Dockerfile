# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:ace83a068839dbfb151b0d80693df23120f6d13f963427fde7e43d9a175fd54a

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
