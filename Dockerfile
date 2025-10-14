# Utiliser une image Java officielle
FROM eclipse-temurin:21-jdk-jammy


# Copier le jar de Spring Boot dans le conteneur
ARG JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Exposer le port 8081 (comme dans ton application.properties)
EXPOSE 8081

# Commande pour lancer l'application
ENTRYPOINT ["java","-jar","/app.jar"]
