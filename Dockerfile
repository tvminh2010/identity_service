FROM openjdk:17-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/identity-microservice-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar							
#hoặc COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]