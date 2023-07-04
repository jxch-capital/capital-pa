FROM openjdk:17-jdk

WORKDIR /app
COPY target/capital-pa.jar /app/capital-pa.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/capital-pa.jar","--spring.profiles.active=prod"]

