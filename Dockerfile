FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring

# Создаем директорию для логов и даем права
RUN mkdir -p /app/logs && chown -R spring:spring /app/logs

WORKDIR /app

COPY --chown=spring:spring product-catalog-app/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]