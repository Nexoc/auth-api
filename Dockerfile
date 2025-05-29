# Используем официальный Java образ
FROM openjdk:21-jdk-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем wrapper и POM отдельно (для кэширования)
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Предварительная сборка зависимостей (кэш)
RUN ./mvnw dependency:go-offline -B

# Копируем исходный код
COPY src ./src

# Сборка проекта
RUN ./mvnw clean package -DskipTests

# Запускаем приложение
CMD ["java", "-jar", "target/auth-0.0.1-SNAPSHOT.jar"]
