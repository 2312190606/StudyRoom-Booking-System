# 直接运行已经打包好的 jar
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
# 将本地 target 目录下的 jar 包拷贝进去
COPY target/study-room-backend-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
