# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /workspace
COPY gradlew ./gradlew
COPY gradle/ ./gradle/
COPY build.gradle settings.gradle ./
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
COPY src/main/ ./src/main/
# El empaquetado no necesita una base de datos; las pruebas se ejecutan aparte.
RUN ./gradlew --no-daemon bootJar && cp build/libs/*.jar /workspace/app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app app
COPY --from=build --chown=app:app /workspace/app.jar /app/app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=render
EXPOSE 10000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
