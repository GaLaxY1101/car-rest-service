FROM openjdk:17-alpine

COPY target/car-rest-service-0.0.1-SNAPSHOT.jar car-rest-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/car-rest-service.jar"]