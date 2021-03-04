FROM openjdk:11-jre

COPY ./live-service.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]