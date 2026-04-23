# syntax=docker/dockerfile:1

FROM gcr.io/distroless/java25:nonroot@sha256:e689f0e2ef32caa54a12928edce92460f69653c68d0a2edff5691fb5116ae5c6

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
