# FROM eclipse-temurin:21-jdk
# WORKDIR /app
# COPY target/*.jar app.jar
# ENTRYPOINT ["java", "-jar", "app.jar"]

FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

# FROM eclipse-temurin:21-jdk
#
# WORKDIR /app
#
# COPY pom.xml .
# COPY mvnw ./
# COPY .mvn .mvn/
# RUN ./mvnw dependency:go-offline
#
# COPY src ./src
#
# EXPOSE 8080
# ENTRYPOINT ["./mvnw", "spring-boot:run"]
