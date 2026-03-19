# Fase de construcción
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiar el archivo pom.xml y descargar las dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar el resto del código fuente y compilar la aplicación
COPY src ./src
RUN mvn package -DskipTests

# Fase de despliegue
FROM openjdk:17-alpine
WORKDIR /app

# Copiar el archivo JAR desde la fase de construcción
COPY --from=build /app/target/interbank-ms-user-1.0.jar app.jar

# Exponer el puerto 9091
EXPOSE 9091

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]