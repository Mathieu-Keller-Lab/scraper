# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:4eadd00d3bff73e6a7491dd36653c1d318ac93fb1fb2cd5eef768fd2b4238408

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
