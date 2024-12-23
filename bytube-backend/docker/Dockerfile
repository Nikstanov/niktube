# Базовый образ с JRE
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем готовый jar-файл в контейнер
COPY bytube-backend-0.0.1-SNAPSHOT.jar app.jar

# Устанавливаем переменные среды
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Открываем порт
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
