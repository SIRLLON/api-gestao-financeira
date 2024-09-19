# Use uma imagem base do OpenJDK
FROM openjdk:17-jdk-alpine

# Copie o arquivo JAR para dentro do container
COPY target/FirstProject-0.0.1-SNAPSHOT.jar /app/FirstProject.jar

# Defina o diretório de trabalho
WORKDIR /app

# Execute o JAR
ENTRYPOINT ["java", "-jar", "FirstProject.jar"]
