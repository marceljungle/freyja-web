# --- Build stage: compile the multi-module backend ---------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy POMs first to leverage Docker layer caching for dependency resolution.
COPY pom.xml ./
COPY freyja-domain/pom.xml freyja-domain/
COPY freyja-application/pom.xml freyja-application/
COPY freyja-infrastructure/pom.xml freyja-infrastructure/
COPY freyja-boot/pom.xml freyja-boot/
RUN mvn -B -ntp -q dependency:go-offline -DskipTests || true

# Copy sources and build the executable jar.
COPY . .
RUN mvn -B -ntp -q -DskipTests clean package

# --- Runtime stage -----------------------------------------------------------
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app
RUN useradd -r -u 1001 -m freyja
COPY --from=build /workspace/freyja-boot/target/freyja-backend.jar /app/app.jar
USER freyja
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
