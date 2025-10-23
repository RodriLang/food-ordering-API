FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copia primero los archivos de dependencias para aprovechar cache de Docker
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Descarga dependencias (se cachea si pom.xml no cambia)
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline

# Copia el código fuente
COPY src src

# Compila la aplicación
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crea un usuario no-root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
# O si necesitas configurar el puerto:
# CMD ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]