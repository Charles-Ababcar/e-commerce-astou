# Utiliser une image de base OpenJDK 17 slim pour garder l'image légère
FROM openjdk:17-slim

# Définir un argument pour le chemin du fichier JAR
ARG JAR_FILE=target/*.jar

# Créer un répertoire de travail
WORKDIR /app

# Copier le fichier JAR de l'application dans le conteneur
COPY ${JAR_FILE} app.jar

# Exposer le port sur lequel l'application s'exécute
EXPOSE 8080

# Définir le point d'entrée pour exécuter l'application
ENTRYPOINT ["java", "-jar", "/app.jar"]
