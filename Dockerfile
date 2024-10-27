# Giai đoạn build

FROM maven:3.8.6-openjdk-11 AS build




# Sao chép toàn bộ mã nguồn vào container
COPY . .

# Chạy lệnh Maven để build ứng dụng
RUN mvn clean install

# Giai đoạn chạy
FROM openjdk:17-jdk-slim


# Sao chép file .jar từ giai đoạn build sang giai đoạn chạy
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar demo.jar

# Khởi động ứng dụng
ENTRYPOINT ["java", "-jar", "demo.jar"]
