# Étape 1 : build de l'application
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
#RUN mvn clean package -DskipTests

# Étape 2 : image runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
RUN mvn clean package -Dmaven.test.skip=true -Dproject.build.sourceEncoding=UTF-8
# Exposer le port de Spring Boot
EXPOSE 8080

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]


