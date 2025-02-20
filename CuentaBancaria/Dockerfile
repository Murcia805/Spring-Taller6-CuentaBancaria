# Etapa 1: hacer el build de la aplicación con Gradle (generar el JAR)
FROM gradle:jdk21 as builder
WORKDIR /app
ENV GRADLE_USER_HOME=/app/.gradle
RUN mkdir -p /app/.gradle && chmod -R 777 /app/.gradle
# Copiado de los archivos de configuración
COPY gradle gradle
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .
# Copiado del código fuente
COPY src src
# Generación del Spring Boot jar
RUN gradle clean bootJar --no-daemon --refresh-dependencies --no-parallel --debug --scan



# Etapa 2: crear un runtime con OpenJDK
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copiar el jar de la Etapa 1
COPY --from=builder /app/build/libs/*.jar app.jar
# Exponer puerto 8080 para comunicación con el micro
EXPOSE 8080
# Correr el jar
ENTRYPOINT ["java", "-jar", "app.jar"]
