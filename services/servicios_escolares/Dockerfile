FROM maven:3.9.11-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src/java src/main/java
COPY src/conf src/main/resources
COPY web/ src/main/webapp/
RUN mvn clean package -DskipTests

FROM tomcat:11.0.11-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/servicios-escolares.war /usr/local/tomcat/webapps/servicios-escolares.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
