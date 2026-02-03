# ======================
# 1️⃣ Build stage
# ======================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /build

# Copiamos el pom primero (cache dependencias)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el código
COPY src ./src

# Compilamos
RUN mvn clean package -DskipTests


# ======================
# 2️⃣ Runtime stage
# ======================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copiamos el JAR generado en el build stage
COPY --from=build /build/target/rec-notification-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
