FROM openjdk:17-jdk-alpine
ARG JAR_FILE=target/*.jar
ARG CHROMEDRIVER=src/main/resources/chromedriver
COPY ${JAR_FILE} app.jar
COPY ${CHROMEDRIVER} ${CHROMEDRIVER}
ENTRYPOINT ["java","-jar","/app.jar"]


