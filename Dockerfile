# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:f25ab728deeafec63d7176a473536f4f4347d42db7e24b3bb0fb7b05ff84d248

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
