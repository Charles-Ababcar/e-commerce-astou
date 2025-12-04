# -------------------------
# Étape 1 : Build Maven
# -------------------------
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app

# Copier uniquement le pom pour le cache
COPY pom.xml .

# Installer les dépendances Maven (optimise le build)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Build en production en utilisant le profil prod
RUN mvn clean package -Pprod -DskipTests -Dproject.build.sourceEncoding=UTF-8

# -------------------------
# Étape 2 : Image runtime
# -------------------------
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copier le jar généré
COPY --from=build /app/target/*.jar app.jar

# Exposer le port Spring Boot
EXPOSE 8080

# Définir le profil Spring actif (prod)
ENV SPRING_PROFILES_ACTIVE=prod

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
