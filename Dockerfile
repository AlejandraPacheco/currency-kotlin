FROM eclipse-temurin:11
COPY target/*.jar app.jar

ENV USERNAME "postgres"
ENV PASSWORD "mysecretpassword"
ENV URL "jdbc:postgresql://localhost:5432/software"


ENTRYPOINT ["java","-jar","/app.jar"]
