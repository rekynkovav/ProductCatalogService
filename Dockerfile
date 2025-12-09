FROM eclipse-temurin:17-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p /app/logs
WORKDIR /app

# Скопируйте JAR из поддиректории
COPY --chown=spring:spring product-catalog-app/target/*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]