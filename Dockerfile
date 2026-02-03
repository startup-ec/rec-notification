# Imagen base Java
FROM eclipse-temurin:17-jdk-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar el jar (asegúrate del nombre)
COPY target/notification-service-*.jar app.jar

# Render inyecta la variable PORT
EXPOSE 8080

# Arranque
ENTRYPOINT ["java","-jar","/app/app.jar"]
