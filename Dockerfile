FROM openjdk:8-jdk-alpine

# Version of the project
ENV VERSION 0.0.1-SNAPSHOT

# The directory where the project will reside
RUN mkdir -p /opt/rezdy
COPY target/lunch-${VERSION}.jar /opt/rezdy

# Rename the jar to a simpler filename
RUN mv /opt/rezdy/lunch-${VERSION}.jar /opt/rezdy/lunch.jar

# The port we are exposing
EXPOSE 8080

# The entry point of the project is the container's only service
ENTRYPOINT ["java", "-jar", "/opt/rezdy/lunch.jar"]