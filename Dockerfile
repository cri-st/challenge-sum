FROM openjdk:18.0.2.1-jdk-slim
RUN apt-get update && apt-get install -y maven
WORKDIR /app
ADD pom.xml .
ADD src src
RUN mvn package -DskipTests
RUN cp target/*.jar /app.jar

EXPOSE 8080
CMD ["java", "-jar", "/app.jar"]