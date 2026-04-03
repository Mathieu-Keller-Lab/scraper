# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:efb9a5000ce8ff56745d56c88c8e61017e674ed053b67e0c44af25ddabf1faa8

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
