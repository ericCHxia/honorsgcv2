FROM openjdk:17.0.2-jdk

MAINTAINER ericxia <ericchxia@gmail.com>
EXPOSE 8080
WORKDIR /work
COPY target/honorv2*.jar /app.jar

ENTRYPOINT ["java","--add-opens","java.base/java.lang=ALL-UNNAMED","-jar","/app.jar"]