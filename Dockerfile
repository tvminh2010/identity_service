#FROM openjdk:17-jdk-alpine
FROM eclipse-temurin:17-jdk-alpine

# Cài đặt thư viện cần thiết cho FontManager của Java
RUN apk add --no-cache \
    ttf-dejavu \
    fontconfig \
    freetype \
    msttcorefonts-installer && \
    update-ms-fonts && \
    fc-cache -f
	
EXPOSE 8080
ARG JAR_FILE=target/identity-microservice-0.0.1.jar
#ADD ${JAR_FILE} app.jar							
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]