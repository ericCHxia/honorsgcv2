FROM openjdk:17.0.2-slim-buster

MAINTAINER ericxia <ericchxia@gmail.com>
EXPOSE 8080
RUN apt-get update; apt-get install -y fontconfig libfreetype6
WORKDIR /work
COPY target/honorv2*.jar /app.jar

ENTRYPOINT ["java","-jar","/app.jar"]