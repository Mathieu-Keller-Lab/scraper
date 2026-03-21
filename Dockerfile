# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:7c2ef3e630046d73ae8372add70a23cbf6f0c334c76c6a9196a21b01abb45559

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
