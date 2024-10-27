 
# Giai đoạn build
FROM maven:3.8.6-openjdk-17 AS build
WORKDIR /app

# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Chạy lệnh Maven để build ứng dụng
RUN mvn clean install

# Giai đoạn chạy
FROM openjdk:17-jdk-slim
WORKDIR /app

# Sao chép file .jar từ giai đoạn build sang giai đoạn chạy
COPY --from=build /app/target/demo.jar app.jar

# Khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
