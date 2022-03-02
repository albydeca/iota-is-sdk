FROM maven:3.8.3-openjdk-16-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package
RUN ls /home/app/target
