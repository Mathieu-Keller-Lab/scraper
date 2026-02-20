# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:2e3d8fc12381605a522cb015f551cb340fd857afb51c71d7decf4fea0d8802d5

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
