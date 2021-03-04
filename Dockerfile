FROM openjdk:11-jre-alpine

COPY ./live-service.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]