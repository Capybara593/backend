# Giai đoạn build
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Chạy lệnh Maven để build ứng dụng và bỏ qua các bài kiểm tra
RUN mvn clean install -DskipTests

# Giai đoạn chạy
FROM openjdk:17-jdk-slim
WORKDIR /app

# Sao chép file .jar từ giai đoạn build sang giai đoạn chạy
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar demo.jar

# Khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "demo.jar"]
