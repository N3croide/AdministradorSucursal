FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY target/*.jar app.jar
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
