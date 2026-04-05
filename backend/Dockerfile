# 第一阶段：构建
FROM maven:3.9.6-eclipse-temurin-17 as builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/study-room-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
